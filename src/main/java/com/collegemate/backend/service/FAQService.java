
package com.collegemate.backend.service;

import java.util.List;

import com.collegemate.backend.dao.FAQRepository;
import com.collegemate.model.FAQ;

public class FAQService {

    private final FAQRepository faqRepository;

    public FAQService() {
        this.faqRepository = new FAQRepository();
    }

    // Add FAQ
    public void addFAQ(
            String question,
            String answer,
            String category) {

        validateFAQ(question, answer, category);

        FAQ faq = new FAQ(
                question.trim(),
                answer.trim(),
                category.trim()
        );

        faqRepository.saveFAQ(faq);
    }

    // Get all FAQs
    public List<FAQ> getAllFAQs() {
        return faqRepository.getAllFAQs();
    }

    // Get FAQs by category
    public List<FAQ> getFAQsByCategory(String category) {

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Category cannot be empty!"
            );
        }

        return faqRepository.getFAQsByCategory(category.trim());
    }

    // Search FAQs
    public List<FAQ> searchFAQs(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return getAllFAQs();
        }

        return faqRepository.searchFAQs(keyword.trim());
    }

    // Update FAQ
    public boolean updateFAQ(
            String oldQuestion,
            String question,
            String answer,
            String category) {

        if (oldQuestion == null || oldQuestion.isBlank()) {
            throw new IllegalArgumentException(
                    "Original question cannot be empty!"
            );
        }

        validateFAQ(question, answer, category);

        FAQ updatedFAQ = new FAQ(
                question.trim(),
                answer.trim(),
                category.trim()
        );

        return faqRepository.updateFAQ(
                oldQuestion.trim(),
                updatedFAQ
        );
    }

    // Delete FAQ
    public boolean deleteFAQ(String question) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Question cannot be empty!"
            );
        }

        return faqRepository.deleteFAQ(question.trim());
    }

    // Validation
    private void validateFAQ(
            String question,
            String answer,
            String category) {

        if (question == null || question.isBlank()
                || answer == null || answer.isBlank()
                || category == null || category.isBlank()) {

            throw new IllegalArgumentException(
                    "Question, answer and category cannot be empty!"
            );
        }
    }
}
