
package com.collegemate.backend.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OpenAIService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenAIService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public String generateResponse(String question) {

        if (question == null || question.isBlank()) {
            return "Please enter a valid question.";
        }

        try {
            ObjectNode requestBody = objectMapper.createObjectNode();

            requestBody.put("model", "llama3.2:3b");
            requestBody.put("prompt", question);
            requestBody.put("stream", false);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:11434/api/generate"
                    ))
                    .timeout(Duration.ofMinutes(3))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            objectMapper.writeValueAsString(requestBody)
                    ))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Ollama API error: " + response.body()
                );
            }

            JsonNode jsonResponse =
                    objectMapper.readTree(response.body());

            return jsonResponse.path("response")
                    .asText("Sorry, I could not generate a response.");

        } catch (Exception e) {
            e.printStackTrace();

            return "Sorry, the local AI service is unavailable. "
                    + "Please check whether Ollama is running.";
        }
    }
}
