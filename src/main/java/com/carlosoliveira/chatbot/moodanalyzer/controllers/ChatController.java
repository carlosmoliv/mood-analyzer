package com.carlosoliveira.chatbot.moodanalyzer.controllers;

import com.carlosoliveira.chatbot.moodanalyzer.dtos.FeedbackMessage;
import com.carlosoliveira.chatbot.moodanalyzer.dtos.InputMessage;
import com.carlosoliveira.chatbot.moodanalyzer.dtos.OutputMessage;
import com.carlosoliveira.chatbot.moodanalyzer.services.AIService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final AIService aiService;

    public ChatController(AIService aiService) {
        this.aiService = aiService;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage handleMessage(InputMessage message) {
        String aiResponse = aiService.generateResponse(message.content());
        String sentiment = aiService.analyzeSentiment(message.content());
        return new OutputMessage(message.sender(), aiResponse, sentiment);
    }

    @MessageMapping("/feedback")
    public void handleFeedback(FeedbackMessage feedback) {
        System.out.println("Feedback received: Message ID = " + feedback.messageId() + ", Correct = " + feedback.isCorrect());
    }
}
