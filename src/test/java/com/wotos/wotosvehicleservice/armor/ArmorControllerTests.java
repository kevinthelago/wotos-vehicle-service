package com.wotos.wotosvehicleservice.armor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArmorController.class)
class ArmorControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArmorService armorService;

    @Test
    void returns200WithArmorProfileWhenPresent() throws Exception {
        ArmorProfile profile = new ArmorProfile(
                5137,
                List.of(
                        new ArmorProfile.ArmorZone("hull_front", 45.0, "hull", List.of(0.0, 0.0, 1.0)),
                        new ArmorProfile.ArmorZone("turret_front", 90.0, "turret", null)
                ),
                ArmorProfile.SOURCE_TANKS_GG,
                Instant.parse("2026-06-01T00:00:00Z"));
        when(armorService.getArmorProfile(5137)).thenReturn(Optional.of(profile));

        mvc.perform(get("/api/vehicles/5137/armor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(5137))
                .andExpect(jsonPath("$.generatedFrom").value("tanks.gg"))
                .andExpect(jsonPath("$.zones[0].key").value("hull_front"))
                .andExpect(jsonPath("$.zones[0].thicknessMm").value(45.0))
                .andExpect(jsonPath("$.zones[0].normalHint[2]").value(1.0))
                // normalHint is omitted when absent (the optional contract field)
                .andExpect(jsonPath("$.zones[1].normalHint").doesNotExist());
    }

    @Test
    void returns404WhenArmorProfileMissing() throws Exception {
        when(armorService.getArmorProfile(9999)).thenReturn(Optional.empty());

        mvc.perform(get("/api/vehicles/9999/armor"))
                .andExpect(status().isNotFound());
    }
}
