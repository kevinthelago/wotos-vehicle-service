package com.wotos.wotosvehicleservice.ingest;

import com.wotos.wotosvehicleservice.armor.ArmorService;
import com.wotos.wotosvehicleservice.ingest.catalog.TankCatalog;
import com.wotos.wotosvehicleservice.ingest.catalog.TankEntry;
import com.wotos.wotosvehicleservice.ingest.filter.IngestFilter;
import com.wotos.wotosvehicleservice.ingest.source.ArmorSource;
import com.wotos.wotosvehicleservice.ingest.source.ModelSource;
import com.wotos.wotosvehicleservice.ingest.storage.ModelStorageService;
import com.wotos.wotosvehicleservice.ingest.storage.VehicleModelAssetRepository;
import com.wotos.wotosvehicleservice.vehicle.Vehicle;
import com.wotos.wotosvehicleservice.vehicle.VehicleCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Orchestrates the per-tank ingest pipeline: persist the vehicle row, fetch and save
 * its armor profile, then fetch and upload its model to object storage.
 *
 * <p>Each tank is processed independently — a failure on tank N is logged and skipped
 * so the run continues from N+1 (resumable). Idempotency: a tank whose armor profile
 * and model asset are both present is skipped unless {@code force} is true.
 */
@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    private final TankCatalog catalog;
    private final ArmorSource armorSource;
    private final ModelSource modelSource;
    private final Optional<ModelStorageService> modelStorageService;
    private final VehicleCatalogService vehicleCatalogService;
    private final ArmorService armorService;
    private final VehicleModelAssetRepository modelAssetRepository;

    public IngestionService(TankCatalog catalog,
                            ArmorSource armorSource,
                            ModelSource modelSource,
                            Optional<ModelStorageService> modelStorageService,
                            VehicleCatalogService vehicleCatalogService,
                            ArmorService armorService,
                            VehicleModelAssetRepository modelAssetRepository) {
        this.catalog = catalog;
        this.armorSource = armorSource;
        this.modelSource = modelSource;
        this.modelStorageService = modelStorageService;
        this.vehicleCatalogService = vehicleCatalogService;
        this.armorService = armorService;
        this.modelAssetRepository = modelAssetRepository;
    }

    public record IngestResult(int attempted, int skipped, int succeeded, int failed) {}

    /**
     * Runs the ingest pipeline for every tank matched by {@code filter}.
     *
     * @param filter selects which catalog entries to process
     * @param force  when true, re-ingests tanks that appear complete already
     */
    public IngestResult ingest(IngestFilter filter, boolean force) {
        List<TankEntry> tanks = catalog.filter(filter);
        int attempted = 0, skipped = 0, succeeded = 0, failed = 0;

        for (TankEntry tank : tanks) {
            attempted++;
            if (!force && isAlreadyIngested(tank.tankId())) {
                log.info("Skipping {} (id={}) — already ingested", tank.name(), tank.tankId());
                skipped++;
                continue;
            }
            try {
                ingestOne(tank);
                succeeded++;
                log.info("Ingested {} (id={})", tank.name(), tank.tankId());
            } catch (Exception e) {
                log.error("Failed to ingest {} (id={}): {}", tank.name(), tank.tankId(), e.getMessage(), e);
                failed++;
                // continue to the next tank — this run is resumable
            }
        }

        log.info("Ingest complete — attempted={} skipped={} succeeded={} failed={}",
                attempted, skipped, succeeded, failed);
        return new IngestResult(attempted, skipped, succeeded, failed);
    }

    // A tank is complete when armor is present AND (storage not configured OR model asset exists).
    private boolean isAlreadyIngested(int tankId) {
        boolean hasArmor = armorService.getArmorProfile(tankId).isPresent();
        boolean hasModel = modelAssetRepository.findByVehicleId(tankId).isPresent();
        return hasArmor && (modelStorageService.isEmpty() || hasModel);
    }

    private void ingestOne(TankEntry entry) {
        vehicleCatalogService.save(buildVehicle(entry));

        armorSource.fetchArmor(entry).ifPresent(armorService::saveArmorProfile);

        modelSource.fetchModel(entry).ifPresent(bytes ->
                modelStorageService.ifPresent(svc ->
                        svc.storeModel(entry.tankId(), canonicalName(entry.name()), bytes)));
    }

    private Vehicle buildVehicle(TankEntry entry) {
        Vehicle v = new Vehicle(entry.tankId(), entry.name(), entry.nation(), entry.tier(), entry.type());
        v.setCanonicalName(canonicalName(entry.name()));
        return v;
    }

    private static String canonicalName(String name) {
        return name.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
