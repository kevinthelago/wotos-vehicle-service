package com.wotos.wotosvehicleservice.client.wot.extra;

import java.util.Map;

public class WotAmmo {

    private final Integer[] penetration;
    private final Map<String, Integer> stun;
    private final String type;
    private final Integer[] damage;

    public WotAmmo(Integer[] penetration, Map<String, Integer> stun, String type, Integer[] damage) {
        this.penetration = penetration;
        this.stun = stun;
        this.type = type;
        this.damage = damage;
    }

    public Integer[] getPenetration() {
        return penetration;
    }

    public Map<String, Integer> getStun() {
        return stun;
    }

    public String getType() {
        return type;
    }

    public Integer[] getDamage() {
        return damage;
    }
}
