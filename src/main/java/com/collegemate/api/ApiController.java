package com.collegemate.api;

import com.collegemate.backend.service.AuthService;
import com.collegemate.backend.service.ChatService;
import com.collegemate.backend.service.ChatHistoryService;
import com.collegemate.backend.service.FAQService;
import com.collegemate.model.AppUser;
import com.collegemate.model.FAQ;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ApiController {

    private final ChatService chatService;
    private final ChatHistoryService historyService;
    private final FAQService faqService;
    private final AuthService authService;
    private final ApiTokenService tokenService;
    private final ObjectMapper objectMapper;

    public ApiController() {
        this.chatService = new ChatService();
        this.historyService = new ChatHistoryService();
        this.faqService = new FAQService();
        this.authService = new AuthService();
        this.tokenService = ApiTokenService.getInstance();
        this.objectMapper = new ObjectMapper();
    }

    public void registerRoutes(HttpServer server) {

        // LOGIN
        server.createContext("/api/auth/login", exchange -> {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405, Map.of("error", "Method not allowed"));
                    return;
                }

                Map<String, String> request = objectMapper.readValue(
                        exchange.getRequestBody(), Map.class
                );

                String email = request.get("email");
                String password = request.get("password");

                if (email == null || password == null
                        || email.isBlank() || password.isBlank()) {
                    sendJson(exchange, 400,
                            Map.of("error", "Email and password are required"));
                    return;
                }

                AppUser user = authService.login(email, password);

                if (user == null) {
                    sendJson(exchange, 401,
                            Map.of("error", "Invalid credentials or inactive account"));
                    return;
                }

                String token = tokenService.issueToken(user);

                sendJson(exchange, 200, Map.of(
                        "status", "success",
                        "token", token,
                        "tokenType", "Bearer",
                        "expiresIn", 28800,
                        "user", Map.of(
                                "userId", user.getUserId(),
                                "name", user.getName(),
                                "email", user.getEmail(),
                                "role", user.getRole()
                        )
                ));

            } catch (IllegalStateException e) {
                sendJson(exchange, 403, Map.of("error", e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, Map.of("error", "Login request failed"));
            }
        });

        // LOGOUT
        server.createContext("/api/auth/logout", exchange -> {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405, Map.of("error", "Method not allowed"));
                    return;
                }

                AppUser user = tokenService.authenticate(exchange);

                if (user == null) {
                    sendJson(exchange, 401,
                            Map.of("error", "Authentication required"));
                    return;
                }

                tokenService.revokeToken(exchange);

                sendJson(exchange, 200, Map.of(
                        "status", "success",
                        "message", "Logged out successfully"
                ));

            } catch (Exception e) {
                sendJson(exchange, 500, Map.of("error", "Logout failed"));
            }
        });

        // FAQ ROUTES
        server.createContext("/api/faqs", exchange -> {
            try {
                AppUser user = tokenService.authenticate(exchange);

                if (user == null) {
                    sendJson(exchange, 401,
                            Map.of("error", "Authentication required"));
                    return;
                }

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

                    List<FAQ> faqs = faqService.getAllFAQs();
                    sendJson(exchange, 200, faqs);

                } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                    if (!"ADMIN".equals(user.getRole())) {
                        sendJson(exchange, 403,
                                Map.of("error", "Admin permission required"));
                        return;
                    }

                    Map<String, String> request = objectMapper.readValue(
                            exchange.getRequestBody(), Map.class
                    );

                    faqService.addFAQ(
                            request.get("question"),
                            request.get("answer"),
                            request.get("category")
                    );

                    sendJson(exchange, 201, Map.of(
                            "status", "success",
                            "message", "FAQ added successfully"
                    ));

                } else {
                    sendJson(exchange, 405,
                            Map.of("error", "Method not allowed"));
                }

            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, Map.of("error", e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500,
                        Map.of("error", "Unable to process FAQ request"));
            }
        });

        // AI CHAT
        server.createContext("/api/chat", exchange -> {
            try {
                AppUser user = tokenService.authenticate(exchange);

                if (user == null) {
                    sendJson(exchange, 401,
                            Map.of("error", "Authentication required"));
                    return;
                }

                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendJson(exchange, 405,
                            Map.of("error", "Method not allowed"));
                    return;
                }

                Map<String, String> request = objectMapper.readValue(
                        exchange.getRequestBody(), Map.class
                );

                String question = request.get("question");
                String conversationId = request.get("conversationId");

                if (question == null || question.isBlank()) {
                    sendJson(exchange, 400,
                            Map.of("error", "Question is required"));
                    return;
                }

                if (conversationId == null || conversationId.isBlank()) {
                    conversationId = historyService.createConversationId();
                } else {
                    conversationId = conversationId.trim();

                    // A supplied conversation must belong to this user.
                    if (historyService.getConversation(
                            user.getUserId(), conversationId).isEmpty()) {
                        sendJson(exchange, 404,
                                Map.of("error", "Conversation not found"));
                        return;
                    }
                }

                historyService.saveMessage(
                        user.getUserId(), conversationId, "USER", question
                );

                String answer = chatService.getResponse(question);

                historyService.saveMessage(
                        user.getUserId(), conversationId, "ASSISTANT", answer
                );

                sendJson(exchange, 200, Map.of(
                        "conversationId", conversationId,
                        "question", question,
                        "answer", answer
                ));

            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, Map.of("error", e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500,
                        Map.of("error", "Unable to process chat request"));
            }
        });

        // CHAT HISTORY
        server.createContext("/api/chat/history", exchange -> {
            try {
                AppUser user = tokenService.authenticate(exchange);

                if (user == null) {
                    sendJson(exchange, 401,
                            Map.of("error", "Authentication required"));
                    return;
                }

                String userId = user.getUserId();

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

                    String conversationId =
                            getQueryParam(exchange, "conversationId");

                    String search =
                            getQueryParam(exchange, "search");

                    if (search != null && !search.isBlank()) {

                        sendJson(exchange, 200,
                                historyService.searchMessages(userId, search));

                    } else if (conversationId != null
                            && !conversationId.isBlank()) {

                        List<?> messages = historyService.getConversation(
                                userId, conversationId
                        );

                        sendJson(exchange, 200, messages);

                    } else {

                        sendJson(exchange, 200,
                                historyService.getConversationIds(userId));
                    }

                } else if ("DELETE".equalsIgnoreCase(
                        exchange.getRequestMethod())) {

                    String conversationId =
                            getQueryParam(exchange, "conversationId");

                    if (conversationId == null || conversationId.isBlank()) {
                        sendJson(exchange, 400,
                                Map.of("error", "Conversation ID is required"));
                        return;
                    }

                    boolean deleted = historyService.deleteConversation(
                            userId, conversationId
                    );

                    if (deleted) {
                        sendJson(exchange, 200, Map.of(
                                "status", "success",
                                "message", "Conversation deleted"
                        ));
                    } else {
                        sendJson(exchange, 404,
                                Map.of("error", "Conversation not found"));
                    }

                } else {
                    sendJson(exchange, 405,
                            Map.of("error", "Method not allowed"));
                }

            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, Map.of("error", e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500,
                        Map.of("error", "Unable to process chat history request"));
            }
        });
    }

    private String getQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getRawQuery();

        if (query == null || query.isBlank()) {
            return null;
        }

        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);

            String name = URLDecoder.decode(
                    parts[0], StandardCharsets.UTF_8
            );

            if (key.equals(name)) {
                return parts.length > 1
                        ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                        : "";
            }
        }

        return null;
    }

    private void sendJson(
            HttpExchange exchange,
            int statusCode,
            Object data
    ) throws IOException {

        byte[] response = objectMapper.writeValueAsString(data)
                .getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}