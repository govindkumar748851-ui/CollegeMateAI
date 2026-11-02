package com.collegemate.backend.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.collegemate.database.MongoDBConnection;
import com.collegemate.model.Placement;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;

public class PlacementRepository {

    private final MongoCollection<Document> collection;

    public PlacementRepository() {
        collection = MongoDBConnection.getCollection("placements");
    }

    public void savePlacement(Placement placement) {
        collection.replaceOne(
                new Document("placementId", placement.getPlacementId()),
                toDocument(placement),
                new ReplaceOptions().upsert(true)
        );
    }

    public List<Placement> getAllPlacements() {
        List<Placement> placements = new ArrayList<>();

        for (Document document : collection.find()) {
            placements.add(convert(document));
        }

        return placements;
    }

    public Placement getPlacementById(String id) {
        Document document = collection.find(
                new Document("placementId", id)
        ).first();

        return document == null ? null : convert(document);
    }

    public boolean updatePlacement(String id, Placement placement) {
        return collection.replaceOne(
                new Document("placementId", id),
                toDocument(placement)
        ).getMatchedCount() > 0;
    }

    public boolean deletePlacement(String id) {
        return collection.deleteOne(
                new Document("placementId", id)
        ).getDeletedCount() > 0;
    }

    public List<Placement> searchPlacements(String keyword) {
        List<Placement> results = new ArrayList<>();

        Document filter = new Document("$or", List.of(
                new Document("companyName",
                        new Document("$regex", keyword).append("$options", "i")),
                new Document("jobRole",
                        new Document("$regex", keyword).append("$options", "i")),
                new Document("location",
                        new Document("$regex", keyword).append("$options", "i"))
        ));

        for (Document document : collection.find(filter)) {
            results.add(convert(document));
        }

        return results;
    }

    private Document toDocument(Placement placement) {
        return new Document("placementId", placement.getPlacementId())
                .append("companyName", placement.getCompanyName())
                .append("jobRole", placement.getJobRole())
                .append("eligibility", placement.getEligibility())
                .append("location", placement.getLocation())
                .append("packageLPA", placement.getPackageLPA())
                .append("applicationDeadline", placement.getApplicationDeadline())
                .append("status", placement.getStatus());
    }

    private Placement convert(Document document) {
        return new Placement(
                document.getString("placementId"),
                document.getString("companyName"),
                document.getString("jobRole"),
                document.getString("eligibility"),
                document.getString("location"),
                document.get("packageLPA") instanceof Number
                        ? ((Number) document.get("packageLPA")).doubleValue() : 0.0,
                document.getString("applicationDeadline"),
                document.getString("status")
        );
    }
}