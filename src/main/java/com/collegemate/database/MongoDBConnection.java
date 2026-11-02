
package com.collegemate.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.InputStream;
import java.util.Properties;

public class MongoDBConnection {

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void connect() {

        try {
            Properties properties = new Properties();

            InputStream input = MongoDBConnection.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            if (input == null) {
                throw new RuntimeException(
                        "config.properties file not found!"
                );
            }

            properties.load(input);
            input.close();

            String uri = properties.getProperty("mongodb.uri");
            String databaseName = properties.getProperty("mongodb.database");

            if (uri == null || uri.isBlank()
                    || databaseName == null || databaseName.isBlank()) {
                throw new RuntimeException(
                        "MongoDB configuration is incomplete!"
                );
            }

            mongoClient = MongoClients.create(uri);

            database = mongoClient.getDatabase(databaseName);

            database.runCommand(new Document("ping", 1));

            System.out.println("MongoDB Connected Successfully!");
            System.out.println("Database: " + databaseName);

        } catch (Exception e) {
            System.out.println("MongoDB Connection Failed!");
            e.printStackTrace();

            closeConnection();

            throw new RuntimeException(
                    "Unable to connect to MongoDB Atlas.", e
            );
        }
    }

    public static MongoDatabase getDatabase() {

        if (database == null) {
            throw new IllegalStateException(
                    "Connect to MongoDB first!"
            );
        }

        return database;
    }

    public static MongoCollection<Document> getCollection(
            String collectionName) {

        return getDatabase().getCollection(collectionName);
    }

    public static void closeConnection() {

        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }
}
