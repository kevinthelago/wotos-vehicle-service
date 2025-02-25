package com.wotos.wotosvehicleservice.client.wot.modules;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotRadio {

    private final Integer tier;
    @JsonProperty("signal_range")
    private final Integer signalRange;
    private final String tag;
    private final String name;
    private final Integer weight;

    public WotRadio(
            Integer tier, Integer signalRange, String tag, String name, Integer weight
    ) {
        this.tier = tier;
        this.signalRange = signalRange;
        this.tag = tag;
        this.name = name;
        this.weight = weight;
    }

    public Integer getTier() {
        return tier;
    }

    public Integer getSignalRange() {
        return signalRange;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public Integer getWeight() {
        return weight;
    }
}
