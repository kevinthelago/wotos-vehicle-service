package com.wotos.wotosvehicleservice.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for the local vehicle mirror, keyed by WoT {@code tank_id}.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
}
