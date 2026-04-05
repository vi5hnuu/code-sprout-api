package com.vi5hnu.codesprout.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "judge")
@Getter
@Setter
public class JudgeConfig {
    private String url;
    private String apiKey;
    /** Shared secret used to sign/verify stream tokens (JUDGE_STREAM_SECRET). */
    private String streamSecret;
}
