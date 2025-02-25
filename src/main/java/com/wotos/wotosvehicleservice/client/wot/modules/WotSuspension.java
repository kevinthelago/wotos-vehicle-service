package com.wotos.wotosvehicleservice.client.wot.modules;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotSuspension {

    private final String name;
    private final Integer weight;
    @JsonProperty("load_limit")
    private final Integer loadLimit;
    private final String tag;
    @JsonProperty("traverse_speed")
    private final Integer traverseSpeed;
    private final Integer tier;
    @JsonProperty("stearing_lock_angle")
    private final Integer stearingLockAngle;

    public WotSuspension(
            String name, Integer weight, Integer loadLimit, String tag,
            Integer traverseSpeed, Integer tier, Integer stearingLockAngle
    ) {
        this.name = name;
        this.weight = weight;
        this.loadLimit = loadLimit;
        this.tag = tag;
        this.traverseSpeed = traverseSpeed;
        this.tier = tier;
        this.stearingLockAngle = stearingLockAngle;
    }

    public String getName() {
        return name;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getLoadLimit() {
        return loadLimit;
    }

    public String getTag() {
        return tag;
    }

    public Integer getTraverseSpeed() {
        return traverseSpeed;
    }

    public Integer getTier() {
        return tier;
    }

    public Integer getStearingLockAngle() {
        return stearingLockAngle;
    }
}
