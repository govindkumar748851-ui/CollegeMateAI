package com.collegemate.model;

public class Placement {

    private String placementId;
    private String companyName;
    private String jobRole;
    private String eligibility;
    private String location;
    private double packageLPA;
    private String applicationDeadline;
    private String status;

    public Placement() {
    }

    public Placement(String placementId, String companyName,
            String jobRole, String eligibility, String location,
            double packageLPA, String applicationDeadline, String status) {

        this.placementId = placementId;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.eligibility = eligibility;
        this.location = location;
        this.packageLPA = packageLPA;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPackageLPA() {
        return packageLPA;
    }

    public void setPackageLPA(double packageLPA) {
        this.packageLPA = packageLPA;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}