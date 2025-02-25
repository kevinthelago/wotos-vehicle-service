package com.wotos.wotosvehicleservice.client.wot;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class WotVehicle {

    @JsonProperty("is_wheeled")
    private final Boolean isWheeled;
    @JsonProperty("radios")
    private final Integer[] radios;
    @JsonProperty("is_premium")
    private final Boolean isPremium;
    private final String tag;
    private final Map<String, String> images;
    @JsonProperty("tank_id")
    private final Integer tankId;
    private final Integer[] suspensions;
    private final Integer[] provisions;
    private final Integer[] engines;
    private final Object[] crew;
    private final String type;
    private final Integer[] guns;
    private final Object multination; // Find out what this looks like
    private final String description;
    @JsonProperty("short_name")
    private final String shortName;
    @JsonProperty("is_premium_igr")
    private final Boolean isPremiumIgr;
    @JsonProperty("next_tanks")
    private final Map<String, Integer> nextTanks;
    @JsonProperty("modules_tree")
    private final Map<String, WotVehicleModule> modulesTree;
    private final String nation;
    private final Integer tier;
    @JsonProperty("prices_xp")
    private final Map<String, Integer> pricesXp;
    private final Boolean isGift;
    private final String name;
    @JsonProperty("price_gold")
    private final Integer priceGold;
    @JsonProperty("price_credit")
    private final Integer priceCredit;
    @JsonProperty("default_profile")
    private final Map<String, Object> defaultProfile;
    private final Integer[] turrets;

    public WotVehicle(
            Boolean isWheeled, Integer[] radios, Boolean isPremium, String tag,
            Map<String, String> images, Integer tankId, Integer[] suspensions,
            Integer[] provisions, Integer[] engines, Object[] crew, String type,
            Integer[] guns, Object multination, String description, String shortName,
            Boolean isPremiumIgr, Map<String, Integer> nextTanks,
            Map<String, WotVehicleModule> modulesTree, String nation, Integer tier,
            Map<String, Integer> pricesXp, Boolean isGift, String name,
            Integer priceGold, Integer priceCredit, Map<String, Object> defaultProfile,
            Integer[] turrets
    ) {
        this.isWheeled = isWheeled;
        this.radios = radios;
        this.isPremium = isPremium;
        this.tag = tag;
        this.images = images;
        this.tankId = tankId;
        this.suspensions = suspensions;
        this.provisions = provisions;
        this.engines = engines;
        this.crew = crew;
        this.type = type;
        this.guns = guns;
        this.multination = multination;
        this.description = description;
        this.shortName = shortName;
        this.isPremiumIgr = isPremiumIgr;
        this.nextTanks = nextTanks;
        this.modulesTree = modulesTree;
        this.nation = nation;
        this.tier = tier;
        this.pricesXp = pricesXp;
        this.isGift = isGift;
        this.name = name;
        this.priceGold = priceGold;
        this.priceCredit = priceCredit;
        this.defaultProfile = defaultProfile;
        this.turrets = turrets;
    }

    public Boolean getWheeled() {
        return isWheeled;
    }

    public Integer[] getRadios() {
        return radios;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public String getTag() {
        return tag;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public Integer getTankId() {
        return tankId;
    }

    public Integer[] getSuspensions() {
        return suspensions;
    }

    public Integer[] getProvisions() {
        return provisions;
    }

    public Integer[] getEngines() {
        return engines;
    }

    public Object[] getCrew() {
        return crew;
    }

    public String getType() {
        return type;
    }

    public Integer[] getGuns() {
        return guns;
    }

    public Object getMultination() {
        return multination;
    }

    public String getDescription() {
        return description;
    }

    public String getShortName() {
        return shortName;
    }

    public Boolean getPremiumIgr() {
        return isPremiumIgr;
    }

    public Map<String, Integer> getNextTanks() {
        return nextTanks;
    }

    public Map<String, WotVehicleModule> getModulesTree() {
        return modulesTree;
    }

    public String getNation() {
        return nation;
    }

    public Integer getTier() {
        return tier;
    }

    public Map<String, Integer> getPricesXp() {
        return pricesXp;
    }

    public Boolean getGift() {
        return isGift;
    }

    public String getName() {
        return name;
    }

    public Integer getPriceGold() {
        return priceGold;
    }

    public Integer getPriceCredit() {
        return priceCredit;
    }

    public Map<String, Object> getDefaultProfile() {
        return defaultProfile;
    }

    public Integer[] getTurrets() {
        return turrets;
    }
}
