package com.wotos.wotosvehicleservice.armor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Reads and upserts vehicle armor profiles. The {@link ArmorProfile} is stored as a
 * JSON document in {@code vehicle_armor.armor_profile_json}; this service is the only
 * place that (de)serializes it, keeping the on-disk shape consistent with the
 * published contract.
 */
@Service
public class ArmorService {

    private final VehicleArmorRepository vehicleArmorRepository;
    private final ObjectMapper objectMapper;

    public ArmorService(VehicleArmorRepository vehicleArmorRepository, ObjectMapper objectMapper) {
        this.vehicleArmorRepository = vehicleArmorRepository;
        this.objectMapper = objectMapper;
    }

    /** @return the latest armor profile for the vehicle, or empty if none ingested yet. */
    public Optional<ArmorProfile> getArmorProfile(Integer vehicleId) {
        return vehicleArmorRepository.findByVehicleId(vehicleId).map(this::toProfile);
    }

    /**
     * Upserts the armor profile for {@code profile.vehicleId()} — one row per vehicle,
     * latest wins. {@code generatedAt} defaults to now when absent.
     */
    public VehicleArmor saveArmorProfile(ArmorProfile profile) {
        VehicleArmor entity = vehicleArmorRepository.findByVehicleId(profile.vehicleId())
                .orElseGet(VehicleArmor::new);
        entity.setVehicleId(profile.vehicleId());
        entity.setArmorProfileJson(write(profile));
        entity.setGeneratedFrom(profile.generatedFrom());
        entity.setGeneratedAt(profile.generatedAt() != null ? profile.generatedAt() : Instant.now());
        return vehicleArmorRepository.save(entity);
    }

    private ArmorProfile toProfile(VehicleArmor entity) {
        try {
            return objectMapper.readValue(entity.getArmorProfileJson(), ArmorProfile.class);
        } catch (JsonProcessingException e) {
            throw new ArmorSerializationException(
                    "Failed to deserialize stored armor profile for vehicle " + entity.getVehicleId(), e);
        }
    }

    private String write(ArmorProfile profile) {
        try {
            return objectMapper.writeValueAsString(profile);
        } catch (JsonProcessingException e) {
            throw new ArmorSerializationException(
                    "Failed to serialize armor profile for vehicle " + profile.vehicleId(), e);
        }
    }
}
