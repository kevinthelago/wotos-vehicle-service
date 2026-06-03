package com.wotos.wotosvehicleservice.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModelController.class)
class ModelControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ModelService modelService;

    @Test
    void returns200WithModelResponseWhenPresent() throws Exception {
        when(modelService.getModel(5137)).thenReturn(Optional.of(new ModelResponse(
                "http://minio:9000/wotos-models/models/t-34-85.glb?X-Amz-Signature=abc",
                "etag123", 2048L, "glb")));

        mvc.perform(get("/api/vehicles/5137/model"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("http://minio:9000/wotos-models/models/t-34-85.glb?X-Amz-Signature=abc"))
                .andExpect(jsonPath("$.etag").value("etag123"))
                .andExpect(jsonPath("$.sizeBytes").value(2048))
                .andExpect(jsonPath("$.format").value("glb"));
    }

    @Test
    void returns404WhenModelMissing() throws Exception {
        when(modelService.getModel(9999)).thenReturn(Optional.empty());

        mvc.perform(get("/api/vehicles/9999/model"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsNonPositiveId() throws Exception {
        mvc.perform(get("/api/vehicles/0/model"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
