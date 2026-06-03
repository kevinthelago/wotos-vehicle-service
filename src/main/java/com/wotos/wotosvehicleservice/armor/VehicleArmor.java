package com.wotos.wotosvehicleservice.armor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/**
 * Persistence row for a vehicle's armor profile — one row per vehicle (latest wins,
 * enforced by a unique key on {@code vehicle_id} and upserted by {@link ArmorService}).
 * The full {@link ArmorProfile} is stored as JSON text in {@code armor_profile_json};
 * {@code generated_from}/{@code generated_at} are duplicated as columns for querying.
 */
@Entity
@Table(name = "vehicle_armor")
public class VehicleArmor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false, unique = true)
    private Integer vehicleId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "armor_profile_json", nullable = false)
    private String armorProfileJson;

    @Column(name = "generated_from", nullable = false)
    private String generatedFrom;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    public VehicleArmor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getArmorProfileJson() {
        return armorProfileJson;
    }

    public void setArmorProfileJson(String armorProfileJson) {
        this.armorProfileJson = armorProfileJson;
    }

    public String getGeneratedFrom() {
        return generatedFrom;
    }

    public void setGeneratedFrom(String generatedFrom) {
        this.generatedFrom = generatedFrom;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
