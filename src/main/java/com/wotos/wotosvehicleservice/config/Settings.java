package com.wotos.wotosvehicleservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Settings {
    public static String WG_APP_ID;

    /**
     * Binds the WoT {@code application_id} from the {@code env.wg-app-id} property,
     * which resolves the {@code WG_APP_ID} environment variable at runtime (injected
     * from GitHub Actions secrets in CI and the deployment secret store in prod). The
     * empty default lets the context start without the secret present — e.g. in
     * tests/CI, where outbound WoT calls are mocked.
     */
    public Settings(
            @Value("${env.wg-app-id:}") String wgAppId
    ) {
        Settings.WG_APP_ID = wgAppId;
    }
}
