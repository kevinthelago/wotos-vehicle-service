package com.wotos.wotosvehicleservice.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelAssetRepository extends JpaRepository<ModelAsset, Long> {

    Optional<ModelAsset> findByVehicleId(Integer vehicleId);
}
