package com.vi5hnu.codesprout.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class CorsConfig {
    private final Environment environment;

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration=new CorsConfiguration();

        if(Arrays.asList(environment.getActiveProfiles()).contains("dev")) configuration.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:4200","http://10.120.240.148:4200"));
        else configuration.setAllowedOrigins(List.of("https://code-sprout.laxmi.solutions","https://the-code-sprout.netlify.app/"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Important if using authentication

        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
}
