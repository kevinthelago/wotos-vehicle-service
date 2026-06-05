package com.wotos.wotosvehicleservice.ingest.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wotos.wotosvehicleservice.armor.ArmorProfile;
import com.wotos.wotosvehicleservice.ingest.catalog.TankEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Reads armor profiles from {@code classpath:tanks/armor/{tankId}.json}. Returns
 * empty when the file is absent so the ingest step proceeds without an armor row.
 * Add a JSON file to {@code src/main/resources/tanks/armor/} for each tank whose
 * profile you want to bundle with the service.
 */
@Component
public class ClasspathArmorSource implements ArmorSource {

    private static final Logger log = LoggerFactory.getLogger(ClasspathArmorSource.class);

    private final ObjectMapper objectMapper;

    public ClasspathArmorSource(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<ArmorProfile> fetchArmor(TankEntry entry) {
        ClassPathResource resource = new ClassPathResource("tanks/armor/" + entry.tankId() + ".json");
        if (!resource.exists()) {
            log.debug("No armor file for tank {} (id={})", entry.name(), entry.tankId());
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(resource.getInputStream(), ArmorProfile.class));
        } catch (Exception e) {
            log.warn("Failed to read armor for tank {} (id={}): {}", entry.name(), entry.tankId(), e.getMessage());
            return Optional.empty();
        }
    }
}
