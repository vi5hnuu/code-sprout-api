package com.vi5hnu.codesprout.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;

@Configuration
@RequiredArgsConstructor
public class S3StorageConfig {
    private final AwsS3Properties properties;

    @Bean(name = "primaryS3Client")
    public S3Client primaryS3Client() {
        return createS3Client(properties.getPrimary());
    }

    @Bean(name = "secondaryS3Client")
    public S3Client secondaryS3Client() {
        return createS3Client(properties.getSecondary());
    }

    // Reusable method to create S3Client with given credentials
    private S3Client createS3Client(AwsS3Properties.AwsCredentials credentials) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(credentials.getAccessKey(), credentials.getSecretKey());
        return S3Client.builder()
                .region(Region.of(credentials.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
