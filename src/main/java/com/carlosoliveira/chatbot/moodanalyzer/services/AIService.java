package com.carlosoliveira.chatbot.moodanalyzer.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

    private static final String HF_API_TOKEN = "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";
    private static final String HF_MODEL = "google/flan-t5-small";
    private static final String HF_ENDPOINT = "https://api-inference.huggingface.co/models/" + HF_MODEL;

    private static final String SENTIMENT_MODEL = "distilbert-base-uncased-finetuned-sst-2-english";
    private static final String SENTIMENT_ENDPOINT = "https://api-inference.huggingface.co/models/" + SENTIMENT_MODEL;


    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateResponse(String prompt) {
        String payload = "{"
                + "\"inputs\": \"" + prompt + "\","
                + "\"parameters\": {\"max_new_tokens\": 50}"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + HF_API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(HF_ENDPOINT, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.get(0).get("generated_text").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I couldn’t understand that.";
        }
    }

    public String analyzeSentimentFromAI(String text) {
        String payload = "{ \"inputs\": \"" + text + "\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + HF_API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(SENTIMENT_ENDPOINT, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode labelNode = root.get(0).get(0).get("label");
            return labelNode.asText(); // Usually "POSITIVE" or "NEGATIVE"
        } catch (Exception e) {
            e.printStackTrace();
            return "Neutral";
        }
    }

    public String analyzeSentiment(String text) {
        String lowerText = text.toLowerCase();
        if (lowerText.contains("happy") || lowerText.contains("good")) {
            return "Positive";
        } else if (lowerText.contains("sad") || lowerText.contains("bad")) {
            return "Negative";
        }
        return "Neutral";
    }
}
