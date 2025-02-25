package com.wotos.wotosvehicleservice.client.wot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wotos.wotosvehicleservice.client.wot.extra.WotAmmo;
import com.wotos.wotosvehicleservice.client.wot.modules.*;

import java.util.Map;

public class WotVehicleCharacteristics {

    private final Map<String, WotEngine> engine;
    private final Object siege; // find out what this looks like
    @JsonProperty("max_ammo")
    private final Integer maxAmmo;
    private final Map<String, WotSuspension> suspension;
    private final Integer weight;
    private final Map<String, Object> armor;
    private final Integer hp;
    @JsonProperty("profile_id")
    private final String profileId;
    private final Map<String, Integer> modules;
    private final Map<String, WotGun> gun;
    @JsonProperty("is_default")
    private final Boolean isDefault;
    private final Map<String, WotTurret> turret;
    @JsonProperty("hull_weight")
    private final Integer hullWeight;
    private final Map<String, WotRadio> radio;
    private final Object rapid;
    @JsonProperty("speed_forward")
    private final Integer speedForward;
    @JsonProperty("hull_hp")
    private final Integer hullHp;
    @JsonProperty("speed_backwards")
    private final Integer speedBackwards;
    @JsonProperty("tank_id")
    private final Integer tankId;
    private final WotAmmo[] wotAmmos;
    @JsonProperty("max_weight")
    private final Integer maxWeight;

    public WotVehicleCharacteristics(
            Map<String, WotEngine> engine, Object siege,
            Integer maxAmmo, Map<String, WotSuspension> suspension,
            Integer weight, Map<String, Object> armor, Integer hp,
            String profileId, Map<String, Integer> modules,
            Map<String, WotGun> gun, Boolean isDefault,
            Map<String, WotTurret> turret, Integer hullWeight,
            Map<String, WotRadio> radio, Object rapid, Integer speedForward,
            Integer hullHp, Integer speedBackwards, Integer tankId,
            WotAmmo[] wotAmmos, Integer maxWeight
    ) {
        this.engine = engine;
        this.siege = siege;
        this.maxAmmo = maxAmmo;
        this.suspension = suspension;
        this.weight = weight;
        this.armor = armor;
        this.hp = hp;
        this.profileId = profileId;
        this.modules = modules;
        this.gun = gun;
        this.isDefault = isDefault;
        this.turret = turret;
        this.hullWeight = hullWeight;
        this.radio = radio;
        this.rapid = rapid;
        this.speedForward = speedForward;
        this.hullHp = hullHp;
        this.speedBackwards = speedBackwards;
        this.tankId = tankId;
        this.wotAmmos = wotAmmos;
        this.maxWeight = maxWeight;
    }

    public Map<String, WotEngine> getEngine() {
        return engine;
    }

    public Object getSiege() {
        return siege;
    }

    public Integer getMaxAmmo() {
        return maxAmmo;
    }

    public Map<String, WotSuspension> getSuspension() {
        return suspension;
    }

    public Integer getWeight() {
        return weight;
    }

    public Map<String, Object> getArmor() {
        return armor;
    }

    public Integer getHp() {
        return hp;
    }

    public String getProfileId() {
        return profileId;
    }

    public Map<String, Integer> getModules() {
        return modules;
    }

    public Map<String, WotGun> getGun() {
        return gun;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public Map<String, WotTurret> getTurret() {
        return turret;
    }

    public Integer getHullWeight() {
        return hullWeight;
    }

    public Map<String, WotRadio> getRadio() {
        return radio;
    }

    public Object getRapid() {
        return rapid;
    }

    public Integer getSpeedForward() {
        return speedForward;
    }

    public Integer getHullHp() {
        return hullHp;
    }

    public Integer getSpeedBackwards() {
        return speedBackwards;
    }

    public Integer getTankId() {
        return tankId;
    }

    public WotAmmo[] getAmmo() {
        return wotAmmos;
    }

    public Integer getMaxWeight() {
        return maxWeight;
    }
}
