package com.carlosoliveira.chatbot.moodanalyzer.dtos;

public record OutputMessage(
        String sender,
        String content,
        String mood
) {
}
