package com.TrainingTracker.TraingingTracker.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String codeforcesBaseUrl,
        List<String> corsAllowedOrigins,
        String swaggerServerUrl
) {
}