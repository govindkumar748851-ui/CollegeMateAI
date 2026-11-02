package com.collegemate.backend.service;

import java.util.List;

import com.collegemate.backend.dao.CourseRepository;
import com.collegemate.model.Course;

public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService() {
        this.courseRepository = new CourseRepository();
    }

    public void addCourse(Course course) {

        validateCourse(course);

        if (courseRepository.getCourseById(course.getCourseId()) != null) {
            throw new IllegalArgumentException("Course ID already exists.");
        }

        courseRepository.saveCourse(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.getAllCourses();
    }

    public Course getCourseById(String courseId) {

        if (courseId == null || courseId.isBlank()) {
            throw new IllegalArgumentException("Course ID is required.");
        }

        return courseRepository.getCourseById(courseId.trim());
    }

    public boolean updateCourse(String courseId, Course updatedCourse) {

        if (courseId == null || courseId.isBlank()) {
            throw new IllegalArgumentException("Course ID is required.");
        }

        validateCourse(updatedCourse);

        return courseRepository.updateCourse(
                courseId.trim(),
                updatedCourse
        );
    }

    public boolean deleteCourse(String courseId) {

        if (courseId == null || courseId.isBlank()) {
            throw new IllegalArgumentException("Course ID is required.");
        }

        return courseRepository.deleteCourse(courseId.trim());
    }

    private void validateCourse(Course course) {

        if (course == null) {
            throw new IllegalArgumentException("Course details are required.");
        }

        if (course.getCourseId() == null || course.getCourseId().isBlank()) {
            throw new IllegalArgumentException("Course ID is required.");
        }

        if (course.getCourseName() == null || course.getCourseName().isBlank()) {
            throw new IllegalArgumentException("Course name is required.");
        }

        if (course.getDepartment() == null || course.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department is required.");
        }

        if (course.getDuration() == null || course.getDuration().isBlank()) {
            throw new IllegalArgumentException("Duration is required.");
        }

        if (course.getFees() < 0) {
            throw new IllegalArgumentException("Fees cannot be negative.");
        }
    }
}