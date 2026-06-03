package com.wotos.wotosvehicleservice.ingest;

import com.wotos.wotosvehicleservice.armor.ArmorProfile;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;

/**
 * Default {@link ArmorSource}: HTTP-GETs the tank's {@code armorSourceUrl}, parses the
 * community document, and normalizes it. Active only under the {@code ingest} profile;
 * tests substitute a stub source.
 */
@Component
@Profile("ingest")
public class HttpArmorSource implements ArmorSource {

    private final RestClient restClient;

    public HttpArmorSource(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public ArmorProfile fetchArmor(Tier1Tank tank) {
        CommunityArmorDocument document = restClient.get()
                .uri(tank.armorSourceUrl())
                .retrieve()
                .body(CommunityArmorDocument.class);
        if (document == null) {
            throw new IllegalStateException("empty armor document for " + tank.name());
        }
        return ArmorNormalizer.normalize(document, tank, Instant.now());
    }
}
