package com.wotos.wotosvehicleservice.ingest;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Default {@link ModelSource}: HTTP-GETs the tank's {@code modelSourceUrl} as raw
 * {@code .glb} bytes. Active only under the {@code ingest} profile.
 */
@Component
@Profile("ingest")
public class HttpModelSource implements ModelSource {

    private final RestClient restClient;

    public HttpModelSource(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public byte[] fetchModel(Tier1Tank tank) {
        byte[] bytes = restClient.get()
                .uri(tank.modelSourceUrl())
                .retrieve()
                .body(byte[].class);
        if (bytes == null || bytes.length == 0) {
            throw new IllegalStateException("empty model for " + tank.name());
        }
        return bytes;
    }
}
