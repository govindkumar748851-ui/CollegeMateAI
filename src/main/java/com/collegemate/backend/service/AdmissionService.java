package com.collegemate.backend.service;

import java.util.List;

import com.collegemate.backend.dao.AdmissionRepository;
import com.collegemate.model.Admission;

public class AdmissionService {

    private final AdmissionRepository repository;

    public AdmissionService() {
        repository = new AdmissionRepository();
    }

    public void addAdmission(Admission admission) {

        validateAdmission(admission);

        if (repository.getAdmissionById(admission.getApplicationId()) != null) {
            throw new IllegalArgumentException(
                    "Application ID already exists."
            );
        }

        repository.saveAdmission(admission);
    }

    public List<Admission> getAllAdmissions() {
        return repository.getAllAdmissions();
    }

    public Admission getAdmissionById(String applicationId) {

        validateApplicationId(applicationId);

        return repository.getAdmissionById(applicationId.trim());
    }

    public boolean updateAdmission(
            String applicationId,
            Admission updatedAdmission) {

        validateApplicationId(applicationId);
        validateAdmission(updatedAdmission);

        return repository.updateAdmission(
                applicationId.trim(),
                updatedAdmission
        );
    }

    public boolean deleteAdmission(String applicationId) {

        validateApplicationId(applicationId);

        return repository.deleteAdmission(applicationId.trim());
    }

    public List<Admission> searchAdmissions(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return getAllAdmissions();
        }

        return repository.searchAdmissions(keyword.trim());
    }

    private void validateAdmission(Admission admission) {

        if (admission == null) {
            throw new IllegalArgumentException(
                    "Admission details are required."
            );
        }

        if (admission.getApplicationId() == null
                || admission.getApplicationId().isBlank()) {
            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }

        if (admission.getStudentName() == null
                || admission.getStudentName().isBlank()) {
            throw new IllegalArgumentException(
                    "Student name is required."
            );
        }

        if (admission.getEmail() == null
                || !admission.getEmail().matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException(
                    "Please enter a valid email address."
            );
        }

        if (admission.getPhone() == null
                || !admission.getPhone().matches("[0-9]{10}")) {
            throw new IllegalArgumentException(
                    "Phone number must contain exactly 10 digits."
            );
        }

        if (admission.getCourseName() == null
                || admission.getCourseName().isBlank()) {
            throw new IllegalArgumentException(
                    "Course name is required."
            );
        }

        if (admission.getAcademicYear() == null
                || admission.getAcademicYear().isBlank()) {
            throw new IllegalArgumentException(
                    "Academic year is required."
            );
        }

        if (admission.getApplicationDate() == null
                || admission.getApplicationDate().isBlank()) {
            throw new IllegalArgumentException(
                    "Application date is required."
            );
        }

        String status = admission.getStatus();

        if (status == null
                || !(status.equals("Pending")
                || status.equals("Approved")
                || status.equals("Rejected"))) {
            throw new IllegalArgumentException(
                    "Status must be Pending, Approved or Rejected."
            );
        }
    }

    private void validateApplicationId(String applicationId) {

        if (applicationId == null || applicationId.isBlank()) {
            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }
    }
}