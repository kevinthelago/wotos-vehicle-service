package com.wotos.wotosvehicleservice.ingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Loads the Tier-1 work-list from {@code classpath:tier1_tanks.json}.
 */
@Component
@Profile("ingest")
public class Tier1TankCatalog {

    private final ObjectMapper objectMapper;

    public Tier1TankCatalog(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Tier1Tank> load() {
        try (InputStream in = new ClassPathResource("tier1_tanks.json").getInputStream()) {
            return objectMapper.readValue(in, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, Tier1Tank.class));
        } catch (IOException e) {
            throw new IllegalStateException("failed to load tier1_tanks.json", e);
        }
    }
}
