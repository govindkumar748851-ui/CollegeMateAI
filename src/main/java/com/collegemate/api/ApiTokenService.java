package com.collegemate.api;

import com.collegemate.backend.dao.AuthRepository;
import com.collegemate.model.AppUser;
import com.sun.net.httpserver.HttpExchange;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApiTokenService {

    private static final ApiTokenService INSTANCE = new ApiTokenService();
    private static final long TOKEN_LIFETIME_SECONDS = 8 * 60 * 60;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, TokenEntry> tokens = new ConcurrentHashMap<>();
    private final AuthRepository authRepository = new AuthRepository();

    private static class TokenEntry {
        private final String userId;
        private final long expiresAt;

        private TokenEntry(String userId, long expiresAt) {
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
    }

    private ApiTokenService() {}

    public static ApiTokenService getInstance() {
        return INSTANCE;
    }

    public String issueToken(AppUser user) {
        if (user == null || user.getUserId() == null
                || !user.isActive()
                || !"APPROVED".equalsIgnoreCase(user.getApprovalStatus())) {
            throw new IllegalArgumentException("Cannot issue token for this account.");
        }

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        long expiry = Instant.now().getEpochSecond() + TOKEN_LIFETIME_SECONDS;
        tokens.put(token, new TokenEntry(user.getUserId(), expiry));

        return token;
    }

    public AppUser authenticate(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        String token = header.substring(7).trim();
        if (token.isEmpty()) {
            return null;
        }

        TokenEntry entry = tokens.get(token);
        if (entry == null) {
            return null;
        }

        if (Instant.now().getEpochSecond() >= entry.expiresAt) {
            tokens.remove(token, entry);
            return null;
        }

        // Always fetch the latest account from MongoDB.
        AppUser currentUser = authRepository.findByUserId(entry.userId);

        if (currentUser == null
                || !currentUser.isActive()
                || !"APPROVED".equalsIgnoreCase(currentUser.getApprovalStatus())
                || !isValidRole(currentUser.getRole())) {
            tokens.remove(token, entry);
            return null;
        }

        return currentUser;
    }

    private boolean isValidRole(String role) {
        return "ADMIN".equals(role)
                || "STAFF".equals(role)
                || "STUDENT".equals(role);
    }

    public boolean revokeToken(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return false;
        }

        String token = header.substring(7).trim();
        return !token.isEmpty() && tokens.remove(token) != null;
    }
}