package com.wotos.wotosvehicleservice.ingest.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleModelAssetRepository extends JpaRepository<VehicleModelAsset, Long> {
    Optional<VehicleModelAsset> findByVehicleId(Integer vehicleId);
}
