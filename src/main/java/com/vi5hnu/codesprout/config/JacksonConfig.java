package com.vi5hnu.codesprout.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {
    private static final Logger logger = LoggerFactory.getLogger(JacksonConfig.class);

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = Jackson2ObjectMapperBuilder
                .json()  // Create an ObjectMapper for JSON
                .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)  // Enable case-insensitive enums
                .featuresToDisable(DeserializationFeature.READ_ENUMS_USING_TO_STRING,DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        logger.info("Custom Jackson ObjectMapper: {}", mapper);
        return mapper;
    }
}
