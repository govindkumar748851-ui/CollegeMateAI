package com.collegemate.model;

public class AppUser {
    private String userId;
    private String name;
    private String email;
    private String passwordHash;
    private String salt;
    private String role;
    private boolean active;
    private String approvalStatus;

    public AppUser() {}

    // Backward compatibility for existing code and users.
    public AppUser(String userId, String name, String email,
                   String passwordHash, String salt, String role, boolean active) {
        this(userId, name, email, passwordHash, salt, role, active, "APPROVED");
    }

    public AppUser(String userId, String name, String email,
                   String passwordHash, String salt, String role,
                   boolean active, String approvalStatus) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.active = active;
        this.approvalStatus = approvalStatus;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public String getApprovalStatus() { return approvalStatus; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setSalt(String salt) { this.salt = salt; }
    public void setRole(String role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
