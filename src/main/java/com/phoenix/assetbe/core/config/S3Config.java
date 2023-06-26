package com.phoenix.assetbe.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.net.URI;


@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getSecretKey());

        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(s3Properties.getBucketUrl())); // bucket 값을 사용하여 엔드포인트 지정

        return builder.build();
    }
}
