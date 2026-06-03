package com.wotos.wotosvehicleservice.armor;

import com.wotos.wotosvehicleservice.web.ResourceNotFoundException;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the latest armor profile for a vehicle. The React garage fetches this to
 * paint armor zones on the 3D mesh; the edge service merges it into the
 * {@code /garage} fan-out. A missing profile throws {@link ResourceNotFoundException},
 * which the global handler renders as a 404 error envelope.
 */
@RestController
@RequestMapping("/api/vehicles")
@Validated
public class ArmorController {

    private final ArmorService armorService;

    public ArmorController(ArmorService armorService) {
        this.armorService = armorService;
    }

    @GetMapping("/{id}/armor")
    public ArmorProfile getArmor(@PathVariable("id") @Positive Integer id) {
        return armorService.getArmorProfile(id)
                .orElseThrow(() -> new ResourceNotFoundException("no armor profile for vehicle " + id));
    }
}
