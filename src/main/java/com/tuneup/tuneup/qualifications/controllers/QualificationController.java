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

    @GetMapping
    public ResponseEntity<Set<QualificationDto>> getAllQualifications() {
        Set<QualificationDto> qualifications = qualificationService.getAllQualifications ();
        return ResponseEntity.ok(qualifications);
    }


    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QualificationDto>> batchCreateQualifications(
            @RequestBody List<QualificationDto> qualificationDtos) {
        List<QualificationDto> savedQualifications = qualificationService.batchCreateQualifications(qualificationDtos);
        return ResponseEntity.status(201).body(savedQualifications);
    }


    @GetMapping("/{id}")
    public ResponseEntity<QualificationDto> getQualificationById(@PathVariable Long id) {
        QualificationDto qualificationDto = qualificationService.getQualificationById(id);
        return ResponseEntity.ok(qualificationDto);
    }

    @PostMapping
    public ResponseEntity<QualificationDto> addQualification(@RequestBody QualificationDto qualificationDto) {

        QualificationDto savedQualification = qualificationService.addQualification(qualificationDto);
        return ResponseEntity.status(201).body(savedQualification);
    }


    @PutMapping("/{id}")
    public ResponseEntity<QualificationDto> updateQualification(
            @PathVariable Long id,
            @RequestBody QualificationDto qualificationDto) {
        QualificationDto updatedQualification = qualificationService.updateQualification(id, qualificationDto);
        return ResponseEntity.ok(updatedQualification);}

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
