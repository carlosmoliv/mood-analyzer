package com.carlosoliveira.chatbot.moodanalyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "huggingface")
public class HuggingFaceConfig {

    private String apiToken;
    private String model;
    private String endpoint;
    private String sentimentModel;
    private String sentimentEndpoint;
}
