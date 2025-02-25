package com.wotos.wotosvehicleservice.client.wot.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotVehicleModule {

    private final String name;
    @JsonProperty("next_modules")
    private final Integer[] nextModules;
    @JsonProperty("next_tanks")
    private final Integer[] nextTanks;
    @JsonProperty("is_default")
    private final Boolean isDefault;
    @JsonProperty("price_xp")
    private final Integer priceXp;
    @JsonProperty("price_credit")
    private final Integer priceCredit;
    @JsonProperty("module_id")
    private final Integer moduleId;
    private final String type;

    public WotVehicleModule(
            String name, Integer[] nextModules, Integer[] nextTanks,
            Boolean isDefault, Integer priceXp, Integer priceCredit,
            Integer moduleId, String type
    ) {
        this.name = name;
        this.nextModules = nextModules;
        this.nextTanks = nextTanks;
        this.isDefault = isDefault;
        this.priceXp = priceXp;
        this.priceCredit = priceCredit;
        this.moduleId = moduleId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Integer[] getNextModules() {
        return nextModules;
    }

    public Integer[] getNextTanks() {
        return nextTanks;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public Integer getPriceXp() {
        return priceXp;
    }

    public Integer getPriceCredit() {
        return priceCredit;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public String getType() {
        return type;
    }
}
