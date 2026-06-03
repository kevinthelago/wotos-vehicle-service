package com.wotos.wotosvehicleservice.storage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Builds minimal-but-valid binary glTF (.glb) byte arrays for tests — a 12-byte
 * header plus a single JSON chunk (no BIN chunk needed for inspection/upload tests).
 */
public final class TestGlbFactory {

    private TestGlbFactory() {
    }

    /** A non-Draco glb whose single triangle-mode primitive yields {@code triangles}. */
    public static byte[] triangleGlb(int triangles) {
        int indexCount = triangles * 3;
        String json = "{\"asset\":{\"version\":\"2.0\"},"
                + "\"accessors\":[{\"count\":" + indexCount + "}],"
                + "\"meshes\":[{\"primitives\":[{\"mode\":4,\"indices\":0,\"attributes\":{\"POSITION\":0}}]}]}";
        return glb(json);
    }

    /** A Draco-compressed glb (declares the required extension) yielding {@code triangles}. */
    public static byte[] dracoTriangleGlb(int triangles) {
        int indexCount = triangles * 3;
        String json = "{\"asset\":{\"version\":\"2.0\"},"
                + "\"extensionsRequired\":[\"KHR_draco_mesh_compression\"],"
                + "\"accessors\":[{\"count\":" + indexCount + "}],"
                + "\"meshes\":[{\"primitives\":[{\"mode\":4,\"indices\":0,"
                + "\"extensions\":{\"KHR_draco_mesh_compression\":{\"bufferView\":0}}}]}]}";
        return glb(json);
    }

    /** Wraps a glTF JSON document in a .glb container (JSON chunk only). */
    public static byte[] glb(String json) {
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        int pad = (4 - (jsonBytes.length % 4)) % 4;
        int jsonChunkLength = jsonBytes.length + pad;
        int total = 12 + 8 + jsonChunkLength;

        ByteBuffer buffer = ByteBuffer.allocate(total).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(0x46546C67); // "glTF"
        buffer.putInt(2);          // version
        buffer.putInt(total);      // total length
        buffer.putInt(jsonChunkLength);
        buffer.putInt(0x4E4F534A); // "JSON"
        buffer.put(jsonBytes);
        for (int i = 0; i < pad; i++) {
            buffer.put((byte) 0x20); // space-pad per glTF spec
        }
        return buffer.array();
    }
}
