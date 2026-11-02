package com.collegemate.backend.dao;

import com.collegemate.database.MongoDBConnection;
import com.collegemate.model.AppUser;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class AuthRepository {
    private final MongoCollection<Document> collection;

    public AuthRepository() {
        collection = MongoDBConnection.getCollection("app_users");
        collection.createIndex(
            new Document("email", 1),
            new com.mongodb.client.model.IndexOptions().unique(true)
        );
    }

    public boolean isEmpty() {
        return collection.countDocuments() == 0;
    }

    public void save(AppUser user) {
        collection.insertOne(toDocument(user));
    }

    public AppUser findByEmail(String email) {
        Document doc = collection.find(eq("email", email.toLowerCase())).first();
        return doc == null ? null : fromDocument(doc);
    }

    public AppUser findByUserId(String userId) {
        Document doc = collection.find(eq("userId", userId)).first();
        return doc == null ? null : fromDocument(doc);
    }
    public List<AppUser> findAll() {
        List<AppUser> users = new ArrayList<>();
        for (Document doc : collection.find().sort(new Document("name", 1))) {
            users.add(fromDocument(doc));
        }
        return users;
    }

    public boolean emailExists(String email) {
        return collection.countDocuments(eq("email", email.toLowerCase())) > 0;
    }

    public void updateActive(String userId, boolean active) {
        collection.updateOne(
            eq("userId", userId),
            new Document("$set", new Document("active", active))
        );
    }

    public void updateApprovalStatus(String userId, String status) {
        collection.updateOne(
            eq("userId", userId),
            new Document("$set", new Document("approvalStatus", status))
        );
    }

    private Document toDocument(AppUser user) {
        return new Document("userId", user.getUserId())
            .append("name", user.getName())
            .append("email", user.getEmail().toLowerCase())
            .append("passwordHash", user.getPasswordHash())
            .append("salt", user.getSalt())
            .append("role", user.getRole())
            .append("active", user.isActive())
            .append("approvalStatus", user.getApprovalStatus());
    }

    private AppUser fromDocument(Document doc) {
        String approvalStatus = doc.getString("approvalStatus");

        // Existing accounts are treated as approved.
        if (approvalStatus == null) {
            approvalStatus = "APPROVED";
        }

        return new AppUser(
            doc.getString("userId"),
            doc.getString("name"),
            doc.getString("email"),
            doc.getString("passwordHash"),
            doc.getString("salt"),
            doc.getString("role"),
            Boolean.TRUE.equals(doc.getBoolean("active")),
            approvalStatus
        );
    }
}
