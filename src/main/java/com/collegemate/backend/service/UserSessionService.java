package com.collegemate.backend.service;

import com.collegemate.database.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public final class UserSessionService {

    private static final Object LOCK = new Object();

    private static String currentSessionId;
    private static ScheduledExecutorService scheduler;

    private UserSessionService() {}

    public static void startSession(String userId) {
        synchronized (LOCK) {
            endCurrentSessionInternal();

            String sessionId = UUID.randomUUID().toString();

            MongoCollection<Document> collection =
                MongoDBConnection.getCollection("user_sessions");

            collection.insertOne(new Document("sessionId", sessionId)
                .append("userId", userId)
                .append("lastSeen", new Date())
                .append("ended", false));

            currentSessionId = sessionId;

            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "CollegeMate-Heartbeat");
                thread.setDaemon(true);
                return thread;
            });

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    heartbeat(sessionId);
                } catch (Exception e) {
                    System.err.println("Heartbeat update failed: " + e.getMessage());
                }
            }, 30, 30, TimeUnit.SECONDS);
        }
    }

    private static void heartbeat(String sessionId) {
        MongoCollection<Document> collection =
            MongoDBConnection.getCollection("user_sessions");

        collection.updateOne(
            eq("sessionId", sessionId),
            new Document("$set", new Document("lastSeen", new Date()))
        );
    }

    public static void endCurrentSession() {
        synchronized (LOCK) {
            endCurrentSessionInternal();
        }
    }

    private static void endCurrentSessionInternal() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }

        if (currentSessionId != null) {
            try {
                MongoCollection<Document> collection =
                    MongoDBConnection.getCollection("user_sessions");

                collection.updateOne(
                    eq("sessionId", currentSessionId),
                    new Document("$set",
                        new Document("ended", true)
                            .append("lastSeen", new Date()))
                );
            } catch (Exception e) {
                System.err.println("Could not end user session: " + e.getMessage());
            }

            currentSessionId = null;
        }
    }

    public static boolean isUserOnline(String userId) {
        MongoCollection<Document> collection =
            MongoDBConnection.getCollection("user_sessions");

        Date cutoff = new Date(System.currentTimeMillis() - 90_000);

        Document query = new Document("userId", userId)
            .append("ended", false)
            .append("lastSeen", new Document("$gte", cutoff));

        return collection.countDocuments(query) > 0;
    }
}