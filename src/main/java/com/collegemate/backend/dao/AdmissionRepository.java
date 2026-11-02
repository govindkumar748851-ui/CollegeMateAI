package com.collegemate.backend.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.collegemate.database.MongoDBConnection;
import com.collegemate.model.Admission;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;

public class AdmissionRepository {

    private final MongoCollection<Document> collection;

    public AdmissionRepository() {
        collection = MongoDBConnection.getCollection("admissions");
    }

    public void saveAdmission(Admission admission) {

        Document document = toDocument(admission);

        collection.replaceOne(
                new Document("applicationId", admission.getApplicationId()),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    public List<Admission> getAllAdmissions() {

        List<Admission> admissions = new ArrayList<>();

        for (Document document : collection.find()) {
            admissions.add(convertToAdmission(document));
        }

        return admissions;
    }

    public Admission getAdmissionById(String applicationId) {

        Document document = collection.find(
                new Document("applicationId", applicationId)
        ).first();

        return document == null ? null : convertToAdmission(document);
    }

    public boolean updateAdmission(
            String applicationId,
            Admission updatedAdmission) {

        return collection.replaceOne(
                new Document("applicationId", applicationId),
                toDocument(updatedAdmission)
        ).getMatchedCount() > 0;
    }

    public boolean deleteAdmission(String applicationId) {

        return collection.deleteOne(
                new Document("applicationId", applicationId)
        ).getDeletedCount() > 0;
    }

    public List<Admission> searchAdmissions(String keyword) {

        List<Admission> admissions = new ArrayList<>();

        Document filter = new Document("$or", List.of(
                new Document("studentName",
                        new Document("$regex", keyword).append("$options", "i")),
                new Document("applicationId",
                        new Document("$regex", keyword).append("$options", "i")),
                new Document("email",
                        new Document("$regex", keyword).append("$options", "i")),
                new Document("courseName",
                        new Document("$regex", keyword).append("$options", "i"))
        ));

        for (Document document : collection.find(filter)) {
            admissions.add(convertToAdmission(document));
        }

        return admissions;
    }

    private Document toDocument(Admission admission) {

        return new Document("applicationId", admission.getApplicationId())
                .append("studentName", admission.getStudentName())
                .append("email", admission.getEmail())
                .append("phone", admission.getPhone())
                .append("courseName", admission.getCourseName())
                .append("academicYear", admission.getAcademicYear())
                .append("applicationDate", admission.getApplicationDate())
                .append("status", admission.getStatus());
    }

    private Admission convertToAdmission(Document document) {

        return new Admission(
                document.getString("applicationId"),
                document.getString("studentName"),
                document.getString("email"),
                document.getString("phone"),
                document.getString("courseName"),
                document.getString("academicYear"),
                document.getString("applicationDate"),
                document.getString("status")
        );
    }
}