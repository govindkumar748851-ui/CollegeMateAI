package com.collegemate.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.collegemate.backend.dao.ChatHistoryRepository;
import com.collegemate.model.ChatMessage;

public class ChatHistoryService {
    private final ChatHistoryRepository repository;

    public ChatHistoryService() {
        repository = new ChatHistoryRepository();
    }

    public String createConversationId() {
        return UUID.randomUUID().toString();
    }

    public void saveMessage(
            String userId, String conversationId,
            String role, String message) {

        if (userId == null || userId.isBlank()
                || conversationId == null || conversationId.isBlank()
                || role == null || role.isBlank()
                || message == null || message.isBlank()) {
            throw new IllegalArgumentException("Invalid chat message or user.");
        }

        if (!List.of("USER", "ASSISTANT").contains(role)) {
            throw new IllegalArgumentException("Invalid message role.");
        }

        repository.save(new ChatMessage(
            conversationId,
            userId,
            role,
            message,
            LocalDateTime.now().toString()
        ));
    }

    public List<ChatMessage> getConversation(
            String userId, String conversationId) {

        validateUser(userId);
        return repository.findByConversation(userId, conversationId);
    }

    public List<String> getConversationIds(String userId) {
        validateUser(userId);
        return repository.findConversationIds(userId);
    }

    public List<ChatMessage> searchMessages(
            String userId, String keyword) {

        validateUser(userId);

        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        return repository.search(userId, keyword.trim());
    }

    public boolean deleteConversation(String userId, String conversationId) {
        validateUser(userId);

        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalArgumentException("Conversation ID is required.");
        }

        return repository.deleteConversation(userId, conversationId.trim());
    }

    private void validateUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Authenticated user is required.");
        }
    }
}