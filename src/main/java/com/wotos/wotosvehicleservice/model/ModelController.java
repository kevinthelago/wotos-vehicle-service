package com.wotos.wotosvehicleservice.model;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves model-asset metadata + a short-lived signed URL for a vehicle's {@code .glb}.
 * The React garage loads the mesh directly from the returned {@code url}.
 */
@RestController
@RequestMapping("/api/vehicles")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @return {@code 200} with {@link ModelResponse} (60s signed URL), or {@code 404}
     *         if the vehicle has no model asset ingested yet.
     */
    @GetMapping("/{id}/model")
    public ResponseEntity<ModelResponse> getModel(@PathVariable("id") Integer id) {
        return modelService.getModel(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
