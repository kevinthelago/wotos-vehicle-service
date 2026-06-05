package com.wotos.wotosvehicleservice.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Creates a {@link MinioClient} bean when {@code storage.s3.access-key} is non-blank.
 * Absent credentials (default in local dev) means no client is registered, so
 * {@code ModelStorageService} is also skipped and model uploads are silently no-ops.
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {

    @Bean
    @Conditional(AccessKeyPresent.class)
    MinioClient minioClient(StorageProperties props) {
        String endpoint = (props.endpoint() == null || props.endpoint().isBlank())
                ? "https://s3." + props.region() + ".amazonaws.com"
                : props.endpoint();
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(props.accessKey(), props.secretKey())
                .build();
    }

    static class AccessKeyPresent implements ConfigurationCondition {
        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return ConfigurationPhase.REGISTER_BEAN;
        }

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String key = context.getEnvironment().getProperty("storage.s3.access-key", "");
            return !key.isBlank();
        }
    }
}
