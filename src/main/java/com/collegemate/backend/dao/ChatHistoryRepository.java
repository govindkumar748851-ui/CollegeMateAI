package com.collegemate.backend.dao;

import com.collegemate.database.MongoDBConnection;
import com.collegemate.model.ChatMessage;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChatHistoryRepository {
    private final MongoCollection<Document> collection;

    public ChatHistoryRepository() {
        collection = MongoDBConnection.getCollection("chat_history");
        collection.createIndex(
            new Document("userId", 1)
                .append("conversationId", 1)
                .append("timestamp", 1)
        );
    }

    public void save(ChatMessage message) {
        collection.insertOne(toDocument(message));
    }

    public List<ChatMessage> findByConversation(
            String userId, String conversationId) {

        List<ChatMessage> messages = new ArrayList<>();

        Document filter = new Document("userId", userId)
            .append("conversationId", conversationId);

        for (Document document : collection.find(filter)
                .sort(new Document("timestamp", 1))) {
            messages.add(convert(document));
        }

        return messages;
    }

    public List<String> findConversationIds(String userId) {
        List<String> ids = new ArrayList<>();

        Document match = new Document("$match",
            new Document("userId", userId)
        );

        Document group = new Document("$group",
            new Document("_id", "$conversationId")
        );

        Document sort = new Document("$sort",
            new Document("_id", -1)
        );

        for (Document document : collection.aggregate(
                List.of(match, group, sort))) {
            String id = document.getString("_id");
            if (id != null) {
                ids.add(id);
            }
        }

        return ids;
    }

    public boolean deleteConversation(String userId, String conversationId) {
        Document filter = new Document("userId", userId)
            .append("conversationId", conversationId);

        return collection.deleteMany(filter).getDeletedCount() > 0;
    }

    public List<ChatMessage> search(String userId, String keyword) {
        List<ChatMessage> results = new ArrayList<>();

        Document filter = new Document("userId", userId)
            .append("message",
                new Document("$regex", Pattern.quote(keyword))
                    .append("$options", "i")
            );

        for (Document document : collection.find(filter)
                .sort(new Document("timestamp", -1))) {
            results.add(convert(document));
        }

        return results;
    }

    private Document toDocument(ChatMessage message) {
        return new Document("conversationId", message.getConversationId())
            .append("userId", message.getUserId())
            .append("role", message.getRole())
            .append("message", message.getMessage())
            .append("timestamp", message.getTimestamp());
    }

    private ChatMessage convert(Document document) {
        return new ChatMessage(
            document.getString("conversationId"),
            document.getString("userId"),
            document.getString("role"),
            document.getString("message"),
            document.getString("timestamp")
        );
    }
}