package com.collegemate.backend.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.collegemate.database.MongoDBConnection;
import com.collegemate.model.StudentPlacement;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;

public class StudentPlacementRepository {

    private final MongoCollection<Document> collection;

    public StudentPlacementRepository() {
        collection = MongoDBConnection.getCollection("student_placements");
    }

    public void save(StudentPlacement application) {
        collection.replaceOne(
                new Document("applicationId", application.getApplicationId()),
                toDocument(application),
                new ReplaceOptions().upsert(true)
        );
    }

    public List<StudentPlacement> findAll() {
        List<StudentPlacement> results = new ArrayList<>();

        for (Document document : collection.find()) {
            results.add(convert(document));
        }

        return results;
    }

    public StudentPlacement findById(String id) {
        Document document = collection.find(
                new Document("applicationId", id)
        ).first();

        return document == null ? null : convert(document);
    }

    public boolean update(String id, StudentPlacement application) {
        return collection.replaceOne(
                new Document("applicationId", id),
                toDocument(application)
        ).getMatchedCount() > 0;
    }

    public boolean delete(String id) {
        return collection.deleteOne(
                new Document("applicationId", id)
        ).getDeletedCount() > 0;
    }

    public List<StudentPlacement> search(String keyword) {
        List<StudentPlacement> results = new ArrayList<>();

        Document filter = new Document("$or", List.of(
                new Document("studentName",
                        new Document("$regex", keyword).append("$options", "i")),
                new Document("studentId",
                        new Document("$regex", keyword).append("$options", "i")),
                new Document("companyName",
                        new Document("$regex", keyword).append("$options", "i")),
                new Document("jobRole",
                        new Document("$regex", keyword).append("$options", "i"))
        ));

        for (Document document : collection.find(filter)) {
            results.add(convert(document));
        }

        return results;
    }

    private Document toDocument(StudentPlacement p) {
        return new Document("applicationId", p.getApplicationId())
                .append("studentId", p.getStudentId())
                .append("studentName", p.getStudentName())
                .append("companyName", p.getCompanyName())
                .append("jobRole", p.getJobRole())
                .append("applicationDate", p.getApplicationDate())
                .append("status", p.getStatus());
    }

    private StudentPlacement convert(Document d) {
        return new StudentPlacement(
                d.getString("applicationId"),
                d.getString("studentId"),
                d.getString("studentName"),
                d.getString("companyName"),
                d.getString("jobRole"),
                d.getString("applicationDate"),
                d.getString("status")
        );
    }
}