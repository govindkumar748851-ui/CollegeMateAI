package com.collegemate.model;

public class ChatMessage {
    private String conversationId;
    private String userId;
    private String role;
    private String message;
    private String timestamp;

    public ChatMessage() {}

    // Backward-compatible constructor.
    public ChatMessage(String conversationId, String role,
                       String message, String timestamp) {
        this(conversationId, null, role, message, timestamp);
    }

    public ChatMessage(String conversationId, String userId,
                       String role, String message, String timestamp) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.role = role;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getConversationId() { return conversationId; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }

    public void setConversationId(String value) { conversationId = value; }
    public void setUserId(String value) { userId = value; }
    public void setRole(String value) { role = value; }
    public void setMessage(String value) { message = value; }
    public void setTimestamp(String value) { timestamp = value; }
}