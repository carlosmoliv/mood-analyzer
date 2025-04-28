package com.carlosoliveira.chatbot.moodanalyzer.services;

import com.carlosoliveira.chatbot.moodanalyzer.config.HuggingFaceConfig;
import com.carlosoliveira.chatbot.moodanalyzer.dtos.InputMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpHeaders headers;
    private final String hfEndpoint;
    private final String sentimentEndpoint;

    public AIService(HuggingFaceConfig config) {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + config.getApiToken());

        this.hfEndpoint = config.getEndpoint();
        this.sentimentEndpoint = config.getSentimentEndpoint();
    }

    public String generateResponse(String prompt) {
        String payload = "{"
                + "\"inputs\": \"" + prompt + "\","
                + "\"parameters\": {\"max_new_tokens\": 50}"
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(hfEndpoint, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.get(0).get("generated_text").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I couldn’t understand that.";
        }
    }

    public String analyzeSentiment(String text) {
        String payload = "{ \"inputs\": \"" + text + "\" }";

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(sentimentEndpoint, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode labels = root.get(0);
            String highestEmotion = "Neutral";
            double highestScore = 0.0;

            for (JsonNode label : labels) {
                String emotion = label.get("label").asText();
                double score = label.get("score").asDouble();
                if (score > highestScore) {
                    highestScore = score;
                    highestEmotion = emotion;
                }
            }
            return highestEmotion;
        } catch (Exception e) {
            e.printStackTrace();
            return "Neutral";
        }
    }
}
