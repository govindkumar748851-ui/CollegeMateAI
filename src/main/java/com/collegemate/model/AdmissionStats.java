package com.collegemate.model;

public class AdmissionStats {

    private int total;
    private int pending;
    private int approved;
    private int rejected;

    public AdmissionStats(int total, int pending, int approved, int rejected) {
        this.total = total;
        this.pending = pending;
        this.approved = approved;
        this.rejected = rejected;
    }

    public int getTotal() {
        return total;
    }

    public int getPending() {
        return pending;
    }

    public int getApproved() {
        return approved;
    }

    public int getRejected() {
        return rejected;
    }
}