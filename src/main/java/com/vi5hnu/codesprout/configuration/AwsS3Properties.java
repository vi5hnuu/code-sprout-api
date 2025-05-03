package com.vi5hnu.codesprout.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Properties {
    private AwsCredentials primary;
    private AwsCredentials secondary;

    @Getter
    @Setter
    public static class AwsCredentials {
        private String accessKey;
        private String secretKey;
        private String region;
        private String bucketName;
    }
}

