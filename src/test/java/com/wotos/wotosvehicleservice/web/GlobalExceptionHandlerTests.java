package com.wotos.wotosvehicleservice.web;

import com.wotos.wotosvehicleservice.model.ModelController;
import com.wotos.wotosvehicleservice.model.ModelService;
import com.wotos.wotosvehicleservice.storage.AssetTooLargeException;
import com.wotos.wotosvehicleservice.storage.StorageException;
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

/**
 * Verifies the global advice renders the standard {@link ApiError} envelope and maps
 * each exception family to the right status (404 not-found, 502 storage, 413 oversize).
 */
@WebMvcTest(ModelController.class)
class GlobalExceptionHandlerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ModelService modelService;

    @Test
    void notFoundProducesEnvelope() throws Exception {
        when(modelService.getModel(9999)).thenReturn(Optional.empty());

        mvc.perform(get("/api/vehicles/9999/model"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("no model asset for vehicle 9999"))
                .andExpect(jsonPath("$.path").value("/api/vehicles/9999/model"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void storageErrorMapsToBadGateway() throws Exception {
        when(modelService.getModel(5137)).thenThrow(new StorageException("minio down"));

        mvc.perform(get("/api/vehicles/5137/model"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message").value("object storage error"));
    }

    @Test
    void oversizeAssetMapsToPayloadTooLarge() throws Exception {
        when(modelService.getModel(6753)).thenThrow(new AssetTooLargeException("too big"));

        mvc.perform(get("/api/vehicles/6753/model"))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.status").value(413));
    }
}
