package com.vi5hnu.codesprout.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public class OAuthClientProperties {
    private Map<String, Registration> registration = new HashMap<>();

    @Getter
    @Setter
    public static class Registration {
        private String clientId;
        private String tokenRevoke;
    }
}


