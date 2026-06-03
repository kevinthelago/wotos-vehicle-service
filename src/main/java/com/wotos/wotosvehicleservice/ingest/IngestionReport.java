package com.wotos.wotosvehicleservice.ingest;

import java.util.List;
import java.util.Map;

/**
 * Summary of an ingestion run. {@code failed} maps a tank id to its failure reason —
 * a failed tank does not abort the run (resumable: the next run retries it).
 */
public record IngestionReport(
        List<Integer> ingested,
        List<Integer> skipped,
        Map<Integer, String> failed
) {

    public int total() {
        return ingested.size() + skipped.size() + failed.size();
    }
}
