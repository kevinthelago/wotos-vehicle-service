package com.wotos.wotosvehicleservice.client.wot.modules;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotGun {

    @JsonProperty("move_down_arc")
    private final Integer moveDownArc;
    private final Integer caliber;
    private final String name;
    private final Integer weight;
    @JsonProperty("move_down_arc")
    private final Integer moveUpArc;
    @JsonProperty("fire_rate")
    private final Float fireRate;
    private final Float dispersion;
    private final String tag;
    @JsonProperty("traverse_speed")
    private final Integer traverseSpeed;
    @JsonProperty("reload_time")
    private final Float reloadTime;
    private final Integer tier;
    @JsonProperty("aim_time")
    private final Float aimTime;

    public WotGun(
            Integer moveDownArc, Integer caliber, String name, Integer weight,
            Integer moveUpArc, Float fireRate, Float dispersion, String tag,
            Integer traverseSpeed, Float reloadTime, Integer tier, Float aimTime
    ) {
        this.moveDownArc = moveDownArc;
        this.caliber = caliber;
        this.name = name;
        this.weight = weight;
        this.moveUpArc = moveUpArc;
        this.fireRate = fireRate;
        this.dispersion = dispersion;
        this.tag = tag;
        this.traverseSpeed = traverseSpeed;
        this.reloadTime = reloadTime;
        this.tier = tier;
        this.aimTime = aimTime;
    }

    public Integer getMoveDownArc() {
        return moveDownArc;
    }

    public Integer getCaliber() {
        return caliber;
    }

    public String getName() {
        return name;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getMoveUpArc() {
        return moveUpArc;
    }

    public Float getFireRate() {
        return fireRate;
    }

    public Float getDispertion() {
        return dispersion;
    }

    public String getTag() {
        return tag;
    }

    public Integer getTraverseSpeed() {
        return traverseSpeed;
    }

    public Float getReloadTime() {
        return reloadTime;
    }

    public Integer getTier() {
        return tier;
    }

    public Float getAimTime() {
        return aimTime;
    }
}
