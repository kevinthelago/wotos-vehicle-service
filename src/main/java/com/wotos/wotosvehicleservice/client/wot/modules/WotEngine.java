package com.wotos.wotosvehicleservice.client.wot.modules;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotEngine {

    private final String name;
    private final Integer power;
    private final Integer weight;
    private final String tag;
    @JsonProperty("fire_chance")
    private final Float fireChance;
    private final Integer tier;

    public WotEngine(String name, Integer power, Integer weight, String tag, Float fireChance, Integer tier) {
        this.name = name;
        this.power = power;
        this.weight = weight;
        this.tag = tag;
        this.fireChance = fireChance;
        this.tier = tier;
    }

    public String getName() {
        return name;
    }

    public Integer getPower() {
        return power;
    }

    public Integer getWeight() {
        return weight;
    }

    public String getTag() {
        return tag;
    }

    public Float getFireChance() {
        return fireChance;
    }

    public Integer getTier() {
        return tier;
    }
}
