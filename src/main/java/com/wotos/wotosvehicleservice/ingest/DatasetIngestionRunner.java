package com.wotos.wotosvehicleservice.ingest;

import com.wotos.wotosvehicleservice.ingest.filter.IngestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * CLI adapter for the ingest pipeline. Activated by passing one of:
 * <pre>
 *   --all                  ingest every tank in the catalog
 *   --tier=N               ingest only tanks at tier N  (1-10)
 *   --nation=X             ingest only tanks of nation X (e.g. ussr, germany)
 * </pre>
 * Optional flag:
 * <pre>
 *   --force                re-ingest tanks that are already complete
 * </pre>
 * When none of {@code --all}, {@code --tier}, {@code --nation} is present the runner
 * returns immediately so the service starts normally as a web server.
 */
@Component
public class DatasetIngestionRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatasetIngestionRunner.class);

    private final IngestionService ingestionService;

    public DatasetIngestionRunner(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        IngestFilter filter = resolveFilter(args);
        if (filter == null) {
            return;
        }
        boolean force = args.containsOption("force");
        log.info("Dataset ingestion starting — filter={} force={}", filter, force);
        ingestionService.ingest(filter, force);
    }

    private IngestFilter resolveFilter(ApplicationArguments args) {
        if (args.containsOption("all")) {
            return IngestFilter.all();
        }
        if (args.containsOption("tier")) {
            String value = args.getOptionValues("tier").get(0);
            return IngestFilter.byTier(Integer.parseInt(value));
        }
        if (args.containsOption("nation")) {
            String value = args.getOptionValues("nation").get(0);
            return IngestFilter.byNation(value);
        }
        return null;
    }
}
