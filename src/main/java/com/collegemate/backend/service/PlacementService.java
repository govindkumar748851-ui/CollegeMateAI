package com.collegemate.backend.service;

import java.util.List;

import com.collegemate.backend.dao.PlacementRepository;
import com.collegemate.model.Placement;

public class PlacementService {

    private final PlacementRepository repository;

    public PlacementService() {
        repository = new PlacementRepository();
    }

    public void addPlacement(Placement placement) {
        validate(placement);

        if (repository.getPlacementById(placement.getPlacementId()) != null) {
            throw new IllegalArgumentException("Placement ID already exists.");
        }

        repository.savePlacement(placement);
    }

    public List<Placement> getAllPlacements() {
        return repository.getAllPlacements();
    }

    public Placement getPlacementById(String id) {
        validateId(id);
        return repository.getPlacementById(id.trim());
    }

    public boolean updatePlacement(String id, Placement placement) {
        validateId(id);
        validate(placement);

        return repository.updatePlacement(id.trim(), placement);
    }

    public boolean deletePlacement(String id) {
        validateId(id);
        return repository.deletePlacement(id.trim());
    }

    public List<Placement> searchPlacements(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllPlacements();
        }

        return repository.searchPlacements(keyword.trim());
    }

    private void validate(Placement placement) {
        if (placement == null) {
            throw new IllegalArgumentException("Placement details are required.");
        }

        if (placement.getPlacementId() == null || placement.getPlacementId().isBlank()
                || placement.getCompanyName() == null || placement.getCompanyName().isBlank()
                || placement.getJobRole() == null || placement.getJobRole().isBlank()
                || placement.getEligibility() == null || placement.getEligibility().isBlank()
                || placement.getLocation() == null || placement.getLocation().isBlank()
                || placement.getApplicationDeadline() == null || placement.getApplicationDeadline().isBlank()) {
            throw new IllegalArgumentException("Please fill all required fields.");
        }

        if (placement.getPackageLPA() < 0) {
            throw new IllegalArgumentException("Package cannot be negative.");
        }

        String status = placement.getStatus();

        if (status == null || !List.of("Open", "Closed", "On Hold").contains(status)) {
            throw new IllegalArgumentException("Status must be Open, Closed or On Hold.");
        }
    }

    private void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Placement ID is required.");
        }
    }
}