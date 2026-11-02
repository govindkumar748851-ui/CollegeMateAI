package com.collegemate.model;

public class StudentPlacement {

    private String applicationId;
    private String studentId;
    private String studentName;
    private String companyName;
    private String jobRole;
    private String applicationDate;
    private String status;

    public StudentPlacement() {}

    public StudentPlacement(String applicationId, String studentId,
            String studentName, String companyName, String jobRole,
            String applicationDate, String status) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.applicationDate = applicationDate;
        this.status = status;
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String value) { applicationId = value; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String value) { studentId = value; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String value) { studentName = value; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String value) { companyName = value; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String value) { jobRole = value; }

    public String getApplicationDate() { return applicationDate; }
    public void setApplicationDate(String value) { applicationDate = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
}