package com.wotos.wotosvehicleservice.ingest.catalog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

/**
 * Loads the tank catalog from {@code classpath:tanks/tanks-catalog.json} and exposes
 * filtered views used by {@link com.wotos.wotosvehicleservice.ingest.IngestionService}.
 * The catalog is read once at startup; the bean is effectively immutable.
 */
@Component
public class TankCatalog {

    private final List<TankEntry> entries;

    public TankCatalog(ObjectMapper objectMapper) throws IOException {
        ClassPathResource resource = new ClassPathResource("tanks/tanks-catalog.json");
        this.entries = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
    }

    public List<TankEntry> filter(Predicate<TankEntry> predicate) {
        return entries.stream().filter(predicate).toList();
    }

    public List<TankEntry> all() {
        return List.copyOf(entries);
    }
}
