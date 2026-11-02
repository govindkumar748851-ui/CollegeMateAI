
package com.collegemate.backend.service;

import java.util.ArrayList;
import java.util.List;

import com.collegemate.backend.ai.NLPProcessor;
import com.collegemate.backend.ai.SimilarityEngine;
import com.collegemate.backend.ai.OpenAIService;
import com.collegemate.model.FAQ;

public class ChatService {

    private final FAQService faqService;
    private final NLPProcessor nlpProcessor;
    private final SimilarityEngine similarityEngine;
    private final OpenAIService openAIService;

    private static final double SIMILARITY_THRESHOLD = 0.20;

    public ChatService() {

        this.faqService = new FAQService();
        this.nlpProcessor = new NLPProcessor();
        this.similarityEngine = new SimilarityEngine();
        this.openAIService = new OpenAIService();
    }

    public String getResponse(String userQuestion) {

        if (userQuestion == null || userQuestion.isBlank()) {
            return "Please enter a question.";
        }

        List<String> processedQuestion =
                nlpProcessor.processQuestion(userQuestion);

        if (processedQuestion.isEmpty()) {
            return "Please ask a meaningful college-related question.";
        }

        List<FAQ> faqs = faqService.getAllFAQs();

        List<String> relevantFAQs = new ArrayList<>();

        for (FAQ faq : faqs) {

            List<String> processedFAQ =
                    nlpProcessor.processQuestion(faq.getQuestion());

            double score = similarityEngine.calculateSimilarity(
                    processedQuestion,
                    processedFAQ
            );

            if (score >= SIMILARITY_THRESHOLD) {

                relevantFAQs.add(
                        "Question: " + faq.getQuestion()
                        + "\nAnswer: " + faq.getAnswer()
                        + "\nCategory: " + faq.getCategory()
                );
            }
        }

        String context;

        if (relevantFAQs.isEmpty()) {
            context = "No relevant college FAQ information was found.";
        } else {
            context = String.join("\n\n", relevantFAQs);
        }

        String prompt = """
                You are CollegeMate AI, a helpful college enquiry assistant.

                Follow these rules:
                1. Answer politely and clearly.
                2. Use the provided college FAQ information as your source.
                3. Never invent college-specific information.
                4. If the provided information does not answer the question,
                   clearly tell the student that the information is unavailable.
                5. You may explain the available information in simple language.

                COLLEGE FAQ INFORMATION:
                %s

                STUDENT QUESTION:
                %s
                """.formatted(context, userQuestion);

        try {

            return openAIService.generateResponse(prompt);

        } catch (Exception e) {

            e.printStackTrace();

            return "Sorry, our AI service is temporarily unavailable. "
                    + "Please try again later.";
        }
    }
}
