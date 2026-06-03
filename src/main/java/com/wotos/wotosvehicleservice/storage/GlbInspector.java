package com.wotos.wotosvehicleservice.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Minimal binary glTF (.glb) inspector. Parses the container header + JSON chunk to
 * derive the triangle count (for the {@code > 100k} upload guard, R4) and detect
 * Draco compression ({@code KHR_draco_mesh_compression}).
 *
 * <p>glb layout: 12-byte header ({@code magic="glTF"}, version, length) followed by
 * chunks {@code [u32 length][u32 type][bytes]}; the first chunk is the glTF JSON.
 * Triangle count is summed over each mesh primitive from its index accessor (or the
 * POSITION accessor when non-indexed), honoring the primitive {@code mode}. Per the
 * glTF spec, accessor {@code count} stays valid even when a primitive is
 * Draco-compressed, so the count is accurate either way.
 */
public final class GlbInspector {

    private static final int MAGIC = 0x46546C67;       // "glTF" little-endian
    private static final int JSON_CHUNK_TYPE = 0x4E4F534A; // "JSON"
    private static final String DRACO_EXTENSION = "KHR_draco_mesh_compression";

    // glTF primitive modes
    private static final int MODE_TRIANGLES = 4;
    private static final int MODE_TRIANGLE_STRIP = 5;
    private static final int MODE_TRIANGLE_FAN = 6;

    private GlbInspector() {
    }

    public record GlbInfo(long triangleCount, boolean dracoCompressed) {
    }

    public static GlbInfo inspect(byte[] glb, ObjectMapper objectMapper) {
        if (glb == null || glb.length < 20) {
            throw new MalformedGlbException("glb is too short to be valid");
        }
        var buffer = java.nio.ByteBuffer.wrap(glb).order(ByteOrder.LITTLE_ENDIAN);
        if (buffer.getInt() != MAGIC) {
            throw new MalformedGlbException("missing glTF magic header");
        }
        buffer.getInt(); // version
        buffer.getInt(); // total length

        int chunkLength = buffer.getInt();
        int chunkType = buffer.getInt();
        if (chunkType != JSON_CHUNK_TYPE) {
            throw new MalformedGlbException("first glb chunk is not JSON");
        }
        if (chunkLength < 0 || chunkLength > buffer.remaining()) {
            throw new MalformedGlbException("declared JSON chunk length exceeds payload");
        }
        byte[] json = new byte[chunkLength];
        buffer.get(json);

        JsonNode root;
        try {
            root = objectMapper.readTree(new String(json, StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new MalformedGlbException("glb JSON chunk is not valid JSON", e);
        }

        boolean draco = hasDracoRequired(root);
        long triangles = 0;
        JsonNode accessors = root.path("accessors");
        for (JsonNode mesh : root.path("meshes")) {
            for (JsonNode primitive : mesh.path("primitives")) {
                if (primitive.path("extensions").has(DRACO_EXTENSION)) {
                    draco = true;
                }
                int mode = primitive.has("mode") ? primitive.get("mode").asInt() : MODE_TRIANGLES;
                int vertexCount = primitiveVertexCount(primitive, accessors);
                triangles += trianglesFor(mode, vertexCount);
            }
        }
        return new GlbInfo(triangles, draco);
    }

    private static boolean hasDracoRequired(JsonNode root) {
        JsonNode required = root.path("extensionsRequired");
        if (required.isArray()) {
            for (JsonNode ext : required) {
                if (DRACO_EXTENSION.equals(ext.asText())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int primitiveVertexCount(JsonNode primitive, JsonNode accessors) {
        if (primitive.has("indices")) {
            return accessors.path(primitive.get("indices").asInt()).path("count").asInt(0);
        }
        JsonNode position = primitive.path("attributes").path("POSITION");
        if (position.isMissingNode()) {
            return 0;
        }
        return accessors.path(position.asInt()).path("count").asInt(0);
    }

    private static long trianglesFor(int mode, int count) {
        return switch (mode) {
            case MODE_TRIANGLES -> count / 3L;
            case MODE_TRIANGLE_STRIP, MODE_TRIANGLE_FAN -> Math.max(0L, count - 2L);
            default -> 0L; // points/lines contribute no triangles
        };
    }
}
