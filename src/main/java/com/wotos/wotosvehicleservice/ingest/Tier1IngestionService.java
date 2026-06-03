package com.wotos.wotosvehicleservice.ingest;

import com.wotos.wotosvehicleservice.armor.ArmorProfile;
import com.wotos.wotosvehicleservice.armor.ArmorService;
import com.wotos.wotosvehicleservice.model.ModelService;
import com.wotos.wotosvehicleservice.storage.ObjectStorageService;
import com.wotos.wotosvehicleservice.storage.StoredObject;
import com.wotos.wotosvehicleservice.vehicle.Vehicle;
import com.wotos.wotosvehicleservice.vehicle.VehicleCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Walks the Tier-1 work-list and, per tank, mirrors the vehicle row, fetches +
 * normalizes its armor profile, and uploads its {@code .glb} to object storage —
 * writing the armor and model-asset rows.
 *
 * <p><b>Idempotent:</b> a tank with both an armor row and a model row is skipped
 * unless {@code force}. <b>Resumable:</b> a per-tank failure is recorded and the run
 * continues, so a later run retries only the unfinished tanks.
 */
@Service
@Profile("ingest")
public class Tier1IngestionService {

    private static final Logger log = LoggerFactory.getLogger(Tier1IngestionService.class);

    private final Tier1TankCatalog catalog;
    private final ArmorSource armorSource;
    private final ModelSource modelSource;
    private final VehicleCatalogService vehicleCatalogService;
    private final ArmorService armorService;
    private final ModelService modelService;
    private final ObjectStorageService objectStorageService;

    public Tier1IngestionService(Tier1TankCatalog catalog, ArmorSource armorSource, ModelSource modelSource,
                                 VehicleCatalogService vehicleCatalogService, ArmorService armorService,
                                 ModelService modelService, ObjectStorageService objectStorageService) {
        this.catalog = catalog;
        this.armorSource = armorSource;
        this.modelSource = modelSource;
        this.vehicleCatalogService = vehicleCatalogService;
        this.armorService = armorService;
        this.modelService = modelService;
        this.objectStorageService = objectStorageService;
    }

    public IngestionReport ingestTier1(boolean force) {
        List<Tier1Tank> tanks = catalog.load();
        List<Integer> ingested = new ArrayList<>();
        List<Integer> skipped = new ArrayList<>();
        Map<Integer, String> failed = new LinkedHashMap<>();

        log.info("starting Tier-1 ingestion of {} tanks (force={})", tanks.size(), force);
        for (Tier1Tank tank : tanks) {
            try {
                if (!force && alreadyIngested(tank.id())) {
                    log.info("skipping {} (id {}) — already ingested", tank.name(), tank.id());
                    skipped.add(tank.id());
                    continue;
                }
                ingestOne(tank);
                ingested.add(tank.id());
                log.info("ingested {} (id {})", tank.name(), tank.id());
            } catch (RuntimeException e) {
                // Resumable: record and continue so one bad source does not abort the run.
                log.warn("failed to ingest {} (id {}): {}", tank.name(), tank.id(), e.getMessage());
                failed.put(tank.id(), e.getMessage());
            }
        }
        log.info("Tier-1 ingestion done: {} ingested, {} skipped, {} failed",
                ingested.size(), skipped.size(), failed.size());
        return new IngestionReport(ingested, skipped, failed);
    }

    private boolean alreadyIngested(Integer id) {
        return armorService.getArmorProfile(id).isPresent() && modelService.hasModel(id);
    }

    private void ingestOne(Tier1Tank tank) {
        // 1. Mirror the vehicle row.
        Vehicle vehicle = new Vehicle(tank.id(), tank.name(), tank.nation(), tank.tier(), tank.type());
        vehicle.setCanonicalName(tank.canonicalName());
        vehicleCatalogService.save(vehicle);

        // 2. Armor profile.
        ArmorProfile armor = armorSource.fetchArmor(tank);
        armorService.saveArmorProfile(armor);

        // 3. Model blob -> object storage -> asset row.
        byte[] glb = modelSource.fetchModel(tank);
        String key = modelKey(tank);
        StoredObject stored = objectStorageService.uploadGlb(key, glb);
        modelService.saveModelAsset(tank.id(), stored);
    }

    private static String modelKey(Tier1Tank tank) {
        return "models/" + tank.nation() + "/" + tank.canonicalName() + ".glb";
    }
}
