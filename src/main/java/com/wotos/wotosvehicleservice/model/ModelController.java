package com.wotos.wotosvehicleservice.model;

import com.wotos.wotosvehicleservice.web.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves model-asset metadata + a short-lived signed URL for a vehicle's {@code .glb}.
 * The React garage loads the mesh directly from the returned {@code url}. A missing
 * asset throws {@link ResourceNotFoundException} (404 error envelope).
 */
@RestController
@RequestMapping("/api/vehicles")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/{id}/model")
    public ModelResponse getModel(@PathVariable("id") Integer id) {
        return modelService.getModel(id)
                .orElseThrow(() -> new ResourceNotFoundException("no model asset for vehicle " + id));
    }
}
