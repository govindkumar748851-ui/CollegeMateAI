
package com.collegemate.backend.dao;

import com.collegemate.database.MongoDBConnection;
import com.collegemate.model.FAQ;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FAQRepository {

    private final MongoCollection<Document> collection;

    public FAQRepository() {
        this.collection = MongoDBConnection.getCollection("faqs");
    }

    // Add FAQ
    public void saveFAQ(FAQ faq) {

        Document document = new Document()
                .append("question", faq.getQuestion())
                .append("answer", faq.getAnswer())
                .append("category", faq.getCategory());

        collection.replaceOne(
                Filters.eq("question", faq.getQuestion()),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    // Get all FAQs
    public List<FAQ> getAllFAQs() {

        List<FAQ> faqs = new ArrayList<>();

        for (Document document : collection.find()) {
            faqs.add(convertToFAQ(document));
        }

        return faqs;
    }

    // Get FAQs by category
    public List<FAQ> getFAQsByCategory(String category) {

        List<FAQ> faqs = new ArrayList<>();

        for (Document document : collection.find(
                Filters.eq("category", category))) {

            faqs.add(convertToFAQ(document));
        }

        return faqs;
    }

    // Search FAQs by question or category
    public List<FAQ> searchFAQs(String keyword) {

        List<FAQ> faqs = new ArrayList<>();

        String pattern = Pattern.quote(keyword);

        for (Document document : collection.find(
                Filters.or(
                        Filters.regex("question", pattern, "i"),
                        Filters.regex("category", pattern, "i")
                ))) {

            faqs.add(convertToFAQ(document));
        }

        return faqs;
    }

    // Update FAQ
    public boolean updateFAQ(
            String oldQuestion,
            FAQ updatedFAQ) {

        Document document = new Document()
                .append("question", updatedFAQ.getQuestion())
                .append("answer", updatedFAQ.getAnswer())
                .append("category", updatedFAQ.getCategory());

        return collection.replaceOne(
                Filters.eq("question", oldQuestion),
                document
        ).getMatchedCount() > 0;
    }

    // Delete FAQ
    public boolean deleteFAQ(String question) {

        return collection.deleteOne(
                Filters.eq("question", question)
        ).getDeletedCount() > 0;
    }

    // Convert MongoDB document into FAQ object
    private FAQ convertToFAQ(Document document) {

        return new FAQ(
                document.getString("question"),
                document.getString("answer"),
                document.getString("category")
        );
    }
}
