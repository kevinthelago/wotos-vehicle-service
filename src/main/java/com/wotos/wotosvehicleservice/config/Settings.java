package com.wotos.wotosvehicleservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Settings {
    public static String WG_APP_ID;

    public Settings(
            @Value("env.wg-app-id") String wgAppId
    ) {
        Settings.WG_APP_ID = wgAppId;
    }
}
