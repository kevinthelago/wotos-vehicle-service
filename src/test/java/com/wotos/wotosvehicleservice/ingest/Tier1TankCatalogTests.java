package com.wotos.wotosvehicleservice.ingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifies tier1_tanks.json parses and holds the 20-tank Tier-1 set. */
class Tier1TankCatalogTests {

    private final Tier1TankCatalog catalog = new Tier1TankCatalog(new ObjectMapper());

    @Test
    void loadsTwentyTanksWithRequiredFields() {
        List<Tier1Tank> tanks = catalog.load();

        assertThat(tanks).hasSize(20);
        assertThat(tanks).allSatisfy(t -> {
            assertThat(t.id()).isNotNull().isPositive();
            assertThat(t.name()).isNotBlank();
            assertThat(t.canonicalName()).isNotBlank();
            assertThat(t.nation()).isNotBlank();
            assertThat(t.type()).isNotBlank();
            assertThat(t.armorSourceUrl()).startsWith("http");
            assertThat(t.modelSourceUrl()).endsWith(".glb");
        });
        // ids are unique (used as the vehicle PK)
        assertThat(tanks.stream().map(Tier1Tank::id).distinct().count()).isEqualTo(20);
        assertThat(tanks).anySatisfy(t -> assertThat(t.name()).isEqualTo("T-34-85"));
    }
}
