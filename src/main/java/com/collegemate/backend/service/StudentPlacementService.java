package com.collegemate.backend.service;

import java.util.List;

import com.collegemate.backend.dao.StudentPlacementRepository;
import com.collegemate.model.StudentPlacement;

public class StudentPlacementService {

    private final StudentPlacementRepository repository;

    public StudentPlacementService() {
        repository = new StudentPlacementRepository();
    }

    public void add(StudentPlacement application) {
        validate(application);

        if (repository.findById(application.getApplicationId()) != null) {
            throw new IllegalArgumentException("Application ID already exists.");
        }

        repository.save(application);
    }

    public List<StudentPlacement> getAll() {
        return repository.findAll();
    }

    public StudentPlacement getById(String id) {
        validateId(id);
        return repository.findById(id.trim());
    }

    public boolean update(String id, StudentPlacement application) {
        validateId(id);
        validate(application);
        return repository.update(id.trim(), application);
    }

    public boolean delete(String id) {
        validateId(id);
        return repository.delete(id.trim());
    }

    public List<StudentPlacement> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        return repository.search(keyword.trim());
    }

    private void validate(StudentPlacement p) {
        if (p == null
                || blank(p.getApplicationId())
                || blank(p.getStudentId())
                || blank(p.getStudentName())
                || blank(p.getCompanyName())
                || blank(p.getJobRole())
                || blank(p.getApplicationDate())) {
            throw new IllegalArgumentException("Please fill all required fields.");
        }

        if (!List.of("Applied", "Shortlisted", "Selected", "Rejected")
                .contains(p.getStatus())) {
            throw new IllegalArgumentException("Invalid placement status.");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private void validateId(String id) {
        if (blank(id)) {
            throw new IllegalArgumentException("Application ID is required.");
        }
    }
}