package com.phoenix.assetbe.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("application.aws")
public class S3Properties {

    private String accessKey;

    private String secretKey;

    private String region;

    private String bucket;

    private String bucketUrl;
}
