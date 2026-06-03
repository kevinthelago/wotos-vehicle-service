package com.wotos.wotosvehicleservice.armor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleArmorRepository extends JpaRepository<VehicleArmor, Long> {

    Optional<VehicleArmor> findByVehicleId(Integer vehicleId);
}
