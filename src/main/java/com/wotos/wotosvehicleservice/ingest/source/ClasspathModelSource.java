package com.wotos.wotosvehicleservice.ingest.source;

import com.wotos.wotosvehicleservice.ingest.catalog.TankEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Reads {@code .glb} model bytes from {@code classpath:tanks/models/{tankId}.glb}.
 * Returns empty when the file is absent so the ingest step proceeds without a model
 * upload. Add a {@code .glb} file to {@code src/main/resources/tanks/models/} for
 * each tank whose model you want to bundle.
 */
@Component
public class ClasspathModelSource implements ModelSource {

    private static final Logger log = LoggerFactory.getLogger(ClasspathModelSource.class);

    @Override
    public Optional<byte[]> fetchModel(TankEntry entry) {
        ClassPathResource resource = new ClassPathResource("tanks/models/" + entry.tankId() + ".glb");
        if (!resource.exists()) {
            log.debug("No model file for tank {} (id={})", entry.name(), entry.tankId());
            return Optional.empty();
        }
        try {
            return Optional.of(resource.getInputStream().readAllBytes());
        } catch (Exception e) {
            log.warn("Failed to read model for tank {} (id={}): {}", entry.name(), entry.tankId(), e.getMessage());
            return Optional.empty();
        }
    }
}
