package com.wotos.wotosvehicleservice.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Builds the AWS SDK v2 S3 client and presigner. Path-style access is forced so the
 * same client works against MinIO (no virtual-host buckets) and S3. When
 * {@code storage.s3.endpoint} is set it overrides the endpoint (MinIO/local); when
 * static credentials are configured they are used, otherwise the default AWS
 * credentials provider chain applies (prod).
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class S3Config {

    @Bean
    public S3Client s3Client(StorageProperties props) {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(props.getRegion()))
                .forcePathStyle(true);
        if (StringUtils.hasText(props.getEndpoint())) {
            builder.endpointOverride(URI.create(props.getEndpoint()));
        }
        if (StringUtils.hasText(props.getAccessKey())) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())));
        }
        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(StorageProperties props) {
        S3Presigner.Builder builder = S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
        if (StringUtils.hasText(props.getEndpoint())) {
            builder.endpointOverride(URI.create(props.getEndpoint()));
        }
        if (StringUtils.hasText(props.getAccessKey())) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())));
        }
        return builder.build();
    }
}
