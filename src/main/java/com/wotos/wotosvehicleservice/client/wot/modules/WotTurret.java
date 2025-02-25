package com.wotos.wotosvehicleservice.client.wot.modules;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotTurret {

    private final String name;
    private final Integer weight;
    @JsonProperty("view_range")
    private final Integer viewRange;
    private final Integer hp;
    private final String tag;
    @JsonProperty("traverse_speed")
    private final String traverseSpeed;
    @JsonProperty("traverse_right_arc")
    private final Integer traverseRightArc;
    private final Integer tier;
    @JsonProperty("traverse_left_arc")
    private final Integer traverseLeftArc;

    public WotTurret(
            String name, Integer weight, Integer viewRange, Integer hp,
            String tag, String traverseSpeed, Integer traverseRightArc,
            Integer tier, Integer traverseLeftArc
    ) {
        this.name = name;
        this.weight = weight;
        this.viewRange = viewRange;
        this.hp = hp;
        this.tag = tag;
        this.traverseSpeed = traverseSpeed;
        this.traverseRightArc = traverseRightArc;
        this.tier = tier;
        this.traverseLeftArc = traverseLeftArc;
    }

    public String getName() {
        return name;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getViewRange() {
        return viewRange;
    }

    public Integer getHp() {
        return hp;
    }

    public String getTag() {
        return tag;
    }

    public String getTraverseSpeed() {
        return traverseSpeed;
    }

    public Integer getTraverseRightArc() {
        return traverseRightArc;
    }

    public Integer getTier() {
        return tier;
    }

    public Integer getTraverseLeftArc() {
        return traverseLeftArc;
    }
}
