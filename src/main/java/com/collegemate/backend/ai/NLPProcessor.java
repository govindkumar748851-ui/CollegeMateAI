
package com.collegemate.backend.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NLPProcessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(
            Arrays.asList(
                    "is", "am", "are", "the", "a", "an",
                    "what", "which", "who", "where",
                    "how", "when", "why",
                    "do", "does", "did",
                    "can", "could", "would", "should",
                    "i", "we", "you", "he", "she", "they",
                    "my", "your", "our", "their",
                    "in", "on", "at", "to", "of",
                    "for", "from", "with", "and", "or",
                    "please", "tell", "me", "about"
            )
    );

    // Convert text to lowercase and remove special characters
    public String normalizeText(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // Remove unnecessary words
    public List<String> removeStopWords(String text) {

        String normalized = normalizeText(text);

        if (normalized.isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(normalized.split("\\s+"))
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toList());
    }

    // Process user's question
    public List<String> processQuestion(String question) {
        return removeStopWords(question);
    }

}
