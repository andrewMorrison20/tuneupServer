package com.tuneup.tuneup.qualifications;

import com.tuneup.tuneup.common.OperationType;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class QualificationValidator {

    private final QualificationRepository qualificationRepository;

    public QualificationValidator(QualificationRepository qualificationRepository) {
        this.qualificationRepository = qualificationRepository;
    }

    // Parent method to validate qualifications based on operation type
    public Qualification validateQualification(OperationType operationType, QualificationDto qualificationDto) {
        Set<String> validationErrors = new HashSet<>();

        Qualification existingQualification = null;

        // Handle CREATE operation validation
        if (operationType == OperationType.CREATE) {
            validateCommon(qualificationDto, validationErrors);
        }

        // Handle UPDATE operation validation
        if (operationType == OperationType.UPDATE) {
            existingQualification = validateExistingQualification(qualificationDto.getId(), validationErrors);
            validateCommon(qualificationDto, validationErrors);
        }

        // Throw exception if there are validation errors
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", validationErrors));
        }
        return existingQualification;
    }

    // Common validation logic for both CREATE and UPDATE operations
    private void validateCommon(QualificationDto qualificationDto, Set<String> validationErrors) {
        if (qualificationDto == null) {
            validationErrors.add("Qualification cannot be null");
            return;
        }

        if (qualificationDto.getName() == null) {
            validationErrors.add("Qualification name cannot be null");
        } else if (!isValidQualificationName(qualificationDto.getName())) {
            validationErrors.add("Invalid qualification name: " + qualificationDto.getName());
        }
    }

    // Validate and fetch an existing qualification for UPDATE operations
    private Qualification validateExistingQualification(Long id, Set<String> validationErrors) {
        if (id == null) {
            validationErrors.add("Qualification ID cannot be null for update");
            return null;
        }

        return qualificationRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Qualification not found with id " + id));
    }

    // Helper method to check if the qualification name is valid
    private boolean isValidQualificationName(QualificationName name) {
        for (QualificationName validName : QualificationName.values()) {
            if (validName == name) {
                return true;
            }
        }
        return false;
    }

    protected Qualification validateAndFetchById(Long id) {
        if (id == null) {
            throw new ValidationException("Qualification ID cannot be null");
        }

        return qualificationRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Qualification not found with id " + id));
    }

    protected void validateDeletion(Long id){
        // Confirm deletion by checking existence
        if (qualificationRepository.existsById(id)) {
            throw new RuntimeException("Failed to delete qualification with id " + id);
        }
    }

}
