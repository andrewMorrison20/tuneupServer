package com.tuneup.tuneup.qualifications;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class QualificationService {

    private final QualificationRepository qualificationRepository;

    @Autowired
    public QualificationService(QualificationRepository qualificationRepository) {
        this.qualificationRepository = qualificationRepository;
    }

    // Fetch all qualifications
    public List<Qualification> getAllQualifications() {
        return qualificationRepository.findAll();
    }

    // Fetch a single qualification by ID
    public Optional<Qualification> getQualificationById(Long id) {
        return qualificationRepository.findById(id);
    }

    // Add a new qualification
    public Qualification addQualification(Qualification qualification) {
        return qualificationRepository.save(qualification);
    }

    // Update an existing qualification
    public Qualification updateQualification(Long id, Qualification updatedQualification) {
        return qualificationRepository.findById(id).map(qualification -> {
            qualification.setName(updatedQualification.getName());
            return qualificationRepository.save(qualification);
        }).orElseThrow(() -> new RuntimeException("Qualification not found with id " + id));
    }

    // Delete a qualification
    public void deleteQualification(Long id) {
        if (qualificationRepository.existsById(id)) {
            qualificationRepository.deleteById(id);
        } else {
            throw new RuntimeException("Qualification not found with id " + id);
        }
    }
}
