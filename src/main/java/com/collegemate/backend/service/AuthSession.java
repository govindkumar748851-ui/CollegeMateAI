package com.collegemate.backend.service;

import com.collegemate.model.AppUser;

public final class AuthSession {
    private static AppUser currentUser;

    private AuthSession() {}

    public static void login(AppUser user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static AppUser getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }
}