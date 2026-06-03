package com.wotos.wotosvehicleservice.ingest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * CLI entry point for the Tier-1 ingestion. Runs only under the {@code ingest} profile
 * and only when {@code --tier1} is passed, so the normal service never ingests:
 *
 * <pre>./mvnw spring-boot:run -Dspring-boot.run.profiles=ingest -Dspring-boot.run.arguments=--tier1</pre>
 *
 * Pass {@code --force} to re-ingest tanks that are already present.
 */
@Component
@Profile("ingest")
public class Tier1IngestionRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(Tier1IngestionRunner.class);

    private final Tier1IngestionService ingestionService;

    public Tier1IngestionRunner(Tier1IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("tier1") && !args.getNonOptionArgs().contains("--tier1")) {
            log.info("ingest profile active but --tier1 not passed; skipping ingestion");
            return;
        }
        boolean force = args.containsOption("force") || args.getNonOptionArgs().contains("--force");
        IngestionReport report = ingestionService.ingestTier1(force);
        log.info("Tier-1 ingestion report: ingested={}, skipped={}, failed={}",
                report.ingested(), report.skipped(), report.failed());
    }
}
