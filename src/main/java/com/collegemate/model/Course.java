package com.collegemate.model;

public class Course {

    private String courseId;
    private String courseName;
    private String department;
    private String duration;
    private String eligibility;
    private double fees;
    private String description;

    public Course() {
    }

    public Course(String courseId, String courseName,
                  String department, String duration,
                  String eligibility, double fees,
                  String description) {

        this.courseId = courseId;
        this.courseName = courseName;
        this.department = department;
        this.duration = duration;
        this.eligibility = eligibility;
        this.fees = fees;
        this.description = description;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
