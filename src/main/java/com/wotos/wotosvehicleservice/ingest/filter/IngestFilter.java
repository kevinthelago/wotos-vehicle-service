package com.wotos.wotosvehicleservice.ingest.filter;

import com.wotos.wotosvehicleservice.ingest.catalog.TankEntry;

import java.util.function.Predicate;

/**
 * Sealed predicate that drives which catalog entries are processed. Composed with
 * {@link com.wotos.wotosvehicleservice.ingest.catalog.TankCatalog#filter}.
 */
public sealed interface IngestFilter extends Predicate<TankEntry>
        permits IngestFilter.All, IngestFilter.ByTier, IngestFilter.ByNation {

    /** Accept every catalog entry. */
    record All() implements IngestFilter {
        @Override
        public boolean test(TankEntry e) { return true; }
    }

    /** Accept only entries whose tier equals {@code tier}. */
    record ByTier(int tier) implements IngestFilter {
        @Override
        public boolean test(TankEntry e) { return e.tier() == tier; }
    }

    /** Accept only entries whose nation matches {@code nation} (case-insensitive). */
    record ByNation(String nation) implements IngestFilter {
        @Override
        public boolean test(TankEntry e) { return nation.equalsIgnoreCase(e.nation()); }
    }

    static IngestFilter all() { return new All(); }
    static IngestFilter byTier(int tier) { return new ByTier(tier); }
    static IngestFilter byNation(String nation) { return new ByNation(nation); }
}
