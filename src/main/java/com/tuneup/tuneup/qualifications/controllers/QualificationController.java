package com.tuneup.tuneup.qualifications.controllers;

import com.tuneup.tuneup.qualifications.dtos.QualificationDto;
import com.tuneup.tuneup.qualifications.services.QualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/qualifications")
public class QualificationController {

    private final QualificationService qualificationService;

    @Autowired
    public QualificationController(QualificationService qualificationService) {
        this.qualificationService = qualificationService;
    }

    /**
     * Retrieve all existing qualifications from the db
     * @return the set of qualifications
     */
    @GetMapping
    public ResponseEntity<Set<QualificationDto>> getAllQualifications() {
        Set<QualificationDto> qualifications = qualificationService.getAllQualifications ();
        return ResponseEntity.ok(qualifications);
    }

    /**
     * Batch create qualifications - for admins
     * @param qualificationDtos the set of qualifications to create
     * @return newly created qualifications
     */
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QualificationDto>> batchCreateQualifications(
            @RequestBody List<QualificationDto> qualificationDtos) {
        List<QualificationDto> savedQualifications = qualificationService.batchCreateQualifications(qualificationDtos);
        return ResponseEntity.status(201).body(savedQualifications);
    }


    /**
     * Retrieve a qualification entity by its Id
     * @param id id of the qualification to retrieve
     * @return QualificationDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<QualificationDto> getQualificationById(@PathVariable Long id) {
        QualificationDto qualificationDto = qualificationService.getQualificationById(id);
        return ResponseEntity.ok(qualificationDto);
    }

    /**
     * Create a single qualification
     * @param qualificationDto qualification to create
     * @return newly created qualification
     */
    @PostMapping
    public ResponseEntity<QualificationDto> addQualification(@RequestBody QualificationDto qualificationDto) {

        QualificationDto savedQualification = qualificationService.addQualification(qualificationDto);
        return ResponseEntity.status(201).body(savedQualification);
    }

    /**
     * Update a qualification
     * @param id id of the qualification to update
     * @param qualificationDto the updated fields
     * @return update qualification
     */
    @PutMapping("/{id}")
    public ResponseEntity<QualificationDto> updateQualification(
            @PathVariable Long id,
            @RequestBody QualificationDto qualificationDto) {
        QualificationDto updatedQualification = qualificationService.updateQualification(id, qualificationDto);
        return ResponseEntity.ok(updatedQualification);}

    /**
     * Delete a qualification by its id
     * @param id id of the qualification to delete
     * @return success status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQualification(@PathVariable Long id) {
        try {
            qualificationService.deleteQualification(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
