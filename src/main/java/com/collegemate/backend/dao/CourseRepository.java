package com.collegemate.backend.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;

import com.collegemate.database.MongoDBConnection;
import com.collegemate.model.Course;

public class CourseRepository {

    private final MongoCollection<Document> collection;

    public CourseRepository() {
        this.collection = MongoDBConnection.getCollection("courses");
    }

    public void saveCourse(Course course) {

        Document document = new Document("courseId", course.getCourseId())
                .append("courseName", course.getCourseName())
                .append("department", course.getDepartment())
                .append("duration", course.getDuration())
                .append("eligibility", course.getEligibility())
                .append("fees", course.getFees())
                .append("description", course.getDescription());

        collection.replaceOne(
                new Document("courseId", course.getCourseId()),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    public List<Course> getAllCourses() {

        List<Course> courses = new ArrayList<>();

        for (Document document : collection.find()) {
            courses.add(convertToCourse(document));
        }

        return courses;
    }

    public Course getCourseById(String courseId) {

        Document document = collection.find(
                new Document("courseId", courseId)
        ).first();

        if (document == null) {
            return null;
        }

        return convertToCourse(document);
    }

    public boolean updateCourse(String courseId, Course updatedCourse) {

        Document document = new Document("courseId", courseId)
                .append("courseName", updatedCourse.getCourseName())
                .append("department", updatedCourse.getDepartment())
                .append("duration", updatedCourse.getDuration())
                .append("eligibility", updatedCourse.getEligibility())
                .append("fees", updatedCourse.getFees())
                .append("description", updatedCourse.getDescription());

        return collection.replaceOne(
                new Document("courseId", courseId),
                document
        ).getMatchedCount() > 0;
    }

    public boolean deleteCourse(String courseId) {

        return collection.deleteOne(
                new Document("courseId", courseId)
        ).getDeletedCount() > 0;
    }

    private Course convertToCourse(Document document) {

        return new Course(
                document.getString("courseId"),
                document.getString("courseName"),
                document.getString("department"),
                document.getString("duration"),
                document.getString("eligibility"),
                document.getDouble("fees") != null
                        ? document.getDouble("fees") : 0.0,
                document.getString("description")
        );
    }
}
