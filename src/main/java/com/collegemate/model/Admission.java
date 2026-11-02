package com.collegemate.model;

public class Admission {

    private String applicationId;
    private String studentName;
    private String email;
    private String phone;
    private String courseName;
    private String academicYear;
    private String applicationDate;
    private String status;

    public Admission() {
    }

    public Admission(
            String applicationId,
            String studentName,
            String email,
            String phone,
            String courseName,
            String academicYear,
            String applicationDate,
            String status) {

        this.applicationId = applicationId;
        this.studentName = studentName;
        this.email = email;
        this.phone = phone;
        this.courseName = courseName;
        this.academicYear = academicYear;
        this.applicationDate = applicationDate;
        this.status = status;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}