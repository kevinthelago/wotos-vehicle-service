package com.wotos.wotosvehicleservice.armor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the latest armor profile for a vehicle. The React garage fetches this to
 * paint armor zones on the 3D mesh; the edge service merges it into the
 * {@code /garage} fan-out.
 */
@RestController
@RequestMapping("/api/vehicles")
public class ArmorController {

    private final ArmorService armorService;

    public ArmorController(ArmorService armorService) {
        this.armorService = armorService;
    }

    /**
     * @return {@code 200} with the latest {@link ArmorProfile}, or {@code 404} if the
     *         vehicle has no armor profile ingested yet.
     */
    @GetMapping("/{id}/armor")
    public ResponseEntity<ArmorProfile> getArmor(@PathVariable("id") Integer id) {
        return armorService.getArmorProfile(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
