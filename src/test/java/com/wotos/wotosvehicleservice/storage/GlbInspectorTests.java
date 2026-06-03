package com.wotos.wotosvehicleservice.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the .glb parser — run everywhere (no Docker needed), covering the
 * triangle-count and Draco-detection logic the upload guards depend on.
 */
class GlbInspectorTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void countsTrianglesFromIndexedPrimitive() {
        GlbInspector.GlbInfo info = GlbInspector.inspect(TestGlbFactory.triangleGlb(100), objectMapper);
        assertThat(info.triangleCount()).isEqualTo(100);
        assertThat(info.dracoCompressed()).isFalse();
    }

    @Test
    void detectsDracoCompression() {
        GlbInspector.GlbInfo info = GlbInspector.inspect(TestGlbFactory.dracoTriangleGlb(50), objectMapper);
        assertThat(info.triangleCount()).isEqualTo(50);
        assertThat(info.dracoCompressed()).isTrue();
    }

    @Test
    void rejectsNonGlbBytes() {
        assertThatThrownBy(() -> GlbInspector.inspect("this is not a glb at all".getBytes(), objectMapper))
                .isInstanceOf(MalformedGlbException.class);
    }

    @Test
    void rejectsTooShortInput() {
        assertThatThrownBy(() -> GlbInspector.inspect(new byte[]{1, 2, 3}, objectMapper))
                .isInstanceOf(MalformedGlbException.class);
    }
}
