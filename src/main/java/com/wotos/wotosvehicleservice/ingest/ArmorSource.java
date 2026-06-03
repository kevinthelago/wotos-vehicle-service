package com.wotos.wotosvehicleservice.ingest;

import com.wotos.wotosvehicleservice.armor.ArmorProfile;

/** Fetches and normalizes a tank's armor profile from its community source. */
public interface ArmorSource {

    ArmorProfile fetchArmor(Tier1Tank tank);
}
