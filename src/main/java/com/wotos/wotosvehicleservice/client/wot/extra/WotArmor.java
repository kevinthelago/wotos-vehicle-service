package com.wotos.wotosvehicleservice.client.wot.extra;

import java.util.Map;

public class WotArmor {

    private final Map<String, WotArmorValues> turret;
    private final Map<String, WotArmorValues> hull;

    public WotArmor(Map<String, WotArmorValues> turret, Map<String, WotArmorValues> hull) {
        this.turret = turret;
        this.hull = hull;
    }

    public Map<String, WotArmorValues> getTurret() {
        return turret;
    }

    public Map<String, WotArmorValues> getHull() {
        return hull;
    }
}
