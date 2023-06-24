package com.phoenix.assetbe.core.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Value("${AWS_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_SECRET_KEY}")
    private String secretKey;

    @Value("${AWS_DEFAULT_REGION}")
    private String region;

    @Bean
    public AmazonS3ClientBuilder amazonS3ClientBuilder() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region);
    }

    @Bean
    public AmazonS3 amazonS3Client() {
        return amazonS3ClientBuilder().build();
    }

    @Bean
    public TransferManager transferManager() {
        return TransferManagerBuilder.standard()
                .withS3Client(amazonS3Client())
                .withMultipartUploadThreshold((long) (10 * 1024 * 1024)) // 10MB로 파일 최소 업로드 파트 크기 설정
                .build();
    }
}