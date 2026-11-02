package com.collegemate.backend.service;

import com.collegemate.backend.dao.AuthRepository;
import com.collegemate.model.AppUser;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class AuthService {
    private final AuthRepository repository = new AuthRepository();
    private final SecureRandom random = new SecureRandom();

    public boolean hasUsers() {
        return !repository.isEmpty();
    }

    public void createFirstAdmin(String name, String email, String password) throws Exception {
        if (!repository.isEmpty()) {
            throw new IllegalStateException("An account already exists.");
        }
        createUser(name, email, password, "ADMIN");
    }

    public void createUser(String name, String email, String password, String role)
            throws Exception {
        validateDetails(name, email, password);

        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) {
            throw new IllegalArgumentException("Invalid role.");
        }

        saveUser(name, email, password, role, true, "APPROVED");
    }

    public void registerPublicUser(String name, String email, String password, String role)
            throws Exception {
        validateDetails(name, email, password);

        if (!"STUDENT".equals(role) && !"STAFF".equals(role)) {
            throw new IllegalArgumentException("Public registration supports Student and Staff only.");
        }

        if ("STUDENT".equals(role)) {
            saveUser(name, email, password, "STUDENT", true, "APPROVED");
        } else {
            saveUser(name, email, password, "STAFF", true, "PENDING");
        }
    }

    private void validateDetails(String name, String email, String password) {
        if (name == null || name.trim().length() < 2) {
            throw new IllegalArgumentException("Enter a valid name.");
        }
        if (email == null ||
            !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters.");
        }
        if (repository.emailExists(email.trim().toLowerCase())) {
            throw new IllegalArgumentException("This email is already registered.");
        }
    }

    private void saveUser(String name, String email, String password,
                          String role, boolean active, String approvalStatus)
            throws Exception {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String salt = Base64.getEncoder().encodeToString(saltBytes);
        String hash = hashPassword(password, saltBytes);

        AppUser user = new AppUser(
            UUID.randomUUID().toString(),
            name.trim(),
            email.trim().toLowerCase(),
            hash,
            salt,
            role,
            active,
            approvalStatus
        );

        repository.save(user);
    }

    public AppUser login(String email, String password) throws Exception {
        if (email == null || password == null) return null;

        AppUser user = repository.findByEmail(email.trim().toLowerCase());

        if (user == null) return null;

        if (!"APPROVED".equalsIgnoreCase(user.getApprovalStatus())) {
            throw new IllegalStateException(
                "Your account is awaiting Admin approval or has been rejected."
            );
        }

        if (!user.isActive()) return null;

        byte[] saltBytes = Base64.getDecoder().decode(user.getSalt());
        String enteredHash = hashPassword(password, saltBytes);

        boolean matches = MessageDigest.isEqual(
            enteredHash.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            user.getPasswordHash().getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        return matches ? user : null;
    }

    public List<AppUser> getAllUsers() {
        return repository.findAll();
    }

    public void setUserActive(String userId, boolean active) {
        repository.updateActive(userId, active);
    }

    public void approveStaff(String userId) {
        repository.updateApprovalStatus(userId, "APPROVED");
    }

    public void rejectStaff(String userId) {
        repository.updateApprovalStatus(userId, "REJECTED");
    }

    private String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120000, 256);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } finally {
            spec.clearPassword();
        }
    }
}
