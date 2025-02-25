package com.wotos.wotosvehicleservice.client.wot.modules;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotModule {

    private final String name;
    private final Integer weight;
    private final String image;
    private final String nation;
    @JsonProperty("price_credit")
    private final String priceCredit;
    private final Integer[] tanks;
    private final Integer tier;
    @JsonProperty("module_id")
    private final Integer moduleId;
    private final String type;

    public WotModule(
            String name, Integer weight, String image,
            String nation, String priceCredit, Integer[] tanks,
            Integer tier, Integer moduleId, String type
    ) {
        this.name = name;
        this.weight = weight;
        this.image = image;
        this.nation = nation;
        this.priceCredit = priceCredit;
        this.tanks = tanks;
        this.tier = tier;
        this.moduleId = moduleId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Integer getWeight() {
        return weight;
    }

    public String getImage() {
        return image;
    }

    public String getNation() {
        return nation;
    }

    public String getPriceCredit() {
        return priceCredit;
    }

    public Integer[] getTanks() {
        return tanks;
    }

    public Integer getTier() {
        return tier;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public String getType() {
        return type;
    }
}
