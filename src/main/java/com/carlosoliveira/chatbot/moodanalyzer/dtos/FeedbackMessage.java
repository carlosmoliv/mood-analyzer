package com.carlosoliveira.chatbot.moodanalyzer.dtos;

public record FeedbackMessage(
        String messageId,
        boolean isCorrect
) {
}
