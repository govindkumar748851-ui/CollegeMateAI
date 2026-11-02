
package com.collegemate.api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import java.util.Map;

public class ApiServer {

    private HttpServer server;

    public void start() throws IOException {

        server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 8080),
                0
        );

        // Health endpoint
        server.createContext("/api/health", exchange -> {

            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, Map.of(
                        "error", "Method not allowed"
                ));
                return;
            }

            sendJson(exchange, 200, Map.of(
                    "status", "success",
                    "message", "CollegeMate AI REST API is running"
            ));
        });

        // Register Chat and FAQ endpoints
        ApiController controller = new ApiController();
        controller.registerRoutes(server);

        server.setExecutor(null);
        server.start();

        System.out.println("=================================");
        System.out.println("CollegeMate AI REST API Started");
        System.out.println("Server: http://localhost:8080");
        System.out.println("Health: http://localhost:8080/api/health");
        System.out.println("FAQs: http://localhost:8080/api/faqs");
        System.out.println("Chat: http://localhost:8080/api/chat");
        System.out.println("=================================");
    }

    private void sendJson(
            com.sun.net.httpserver.HttpExchange exchange,
            int statusCode,
            Object data
    ) throws IOException {

        String response = new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(data);

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    public void stop() {

        if (server != null) {
            server.stop(0);
            System.out.println("REST API Server Stopped");
        }
    }
}
