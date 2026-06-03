package com.wotos.wotosvehicleservice.vehicle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Local mirror of a WoT Tankopedia vehicle plus our own augmentation fields.
 * The primary key {@code id} is the WoT {@code tank_id} (assigned upstream, never
 * generated). Flyway owns the {@code vehicles} table; this entity is validated
 * against it at startup.
 */
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "short_name")
    private String shortName;

    private String nation;

    private Integer tier;

    private String type;

    /** Combat weight in kilograms (from the WoT default_profile). */
    @Column(name = "weight_kg")
    private Integer weightKg;

    /** Hull traverse speed in degrees/second (from the WoT default_profile suspension). */
    @Column(name = "traverse_speed_deg_s")
    private Integer traverseSpeedDegS;

    /** Our slug, e.g. {@code "t-34-85"} — stable identifier used by the garage. */
    @Column(name = "canonical_name")
    private String canonicalName;

    /** Garage camera preset key. */
    @Column(name = "default_camera")
    private String defaultCamera;

    /** Augmentation: shell-picker data for the garage, stored as JSON text. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "shell_types")
    private String shellTypes;

    public Vehicle() {
    }

    public Vehicle(Integer id, String name, String nation, Integer tier, String type) {
        this.id = id;
        this.name = name;
        this.nation = nation;
        this.tier = tier;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Integer weightKg) {
        this.weightKg = weightKg;
    }

    public Integer getTraverseSpeedDegS() {
        return traverseSpeedDegS;
    }

    public void setTraverseSpeedDegS(Integer traverseSpeedDegS) {
        this.traverseSpeedDegS = traverseSpeedDegS;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getDefaultCamera() {
        return defaultCamera;
    }

    public void setDefaultCamera(String defaultCamera) {
        this.defaultCamera = defaultCamera;
    }

    public String getShellTypes() {
        return shellTypes;
    }

    public void setShellTypes(String shellTypes) {
        this.shellTypes = shellTypes;
    }
}
