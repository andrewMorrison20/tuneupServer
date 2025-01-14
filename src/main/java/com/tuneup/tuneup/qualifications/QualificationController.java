package com.tuneup.tuneup.qualifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.tuneup.tuneup.qualifications.QualificationDto;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/qualifications")
public class QualificationController {

    private final QualificationService qualificationService;

    @Autowired
    public QualificationController(QualificationService qualificationService) {
        this.qualificationService = qualificationService;
    }

    // Fetch all qualifications
    @GetMapping
    public ResponseEntity<Set<QualificationDto>> getAllQualifications() {
        Set<QualificationDto> qualifications = qualificationService.getAllQualifications ();
        return ResponseEntity.ok(qualifications);
    }

    // Add multiple qualifications
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QualificationDto>> batchCreateQualifications(
            @RequestBody List<QualificationDto> qualificationDtos) {
        List<QualificationDto> savedQualifications = qualificationService.batchCreateQualifications(qualificationDtos);
        return ResponseEntity.status(201).body(savedQualifications);
    }



    // Fetch a single qualification by ID
    @GetMapping("/{id}")
    public ResponseEntity<QualificationDto> getQualificationById(@PathVariable Long id) {
        QualificationDto qualificationDto = qualificationService.getQualificationById(id);
        return ResponseEntity.ok(qualificationDto);
    }

    // Add a new qualification
    @PostMapping
    public ResponseEntity<QualificationDto> addQualification(@RequestBody QualificationDto qualificationDto) {

        QualificationDto savedQualification = qualificationService.addQualification(qualificationDto);
        return ResponseEntity.status(201).body(savedQualification);
    }

    // Update an existing qualification
    @PutMapping("/{id}")
    public ResponseEntity<QualificationDto> updateQualification(
            @PathVariable Long id,
            @RequestBody QualificationDto qualificationDto) {
        QualificationDto updatedQualification = qualificationService.updateQualification(id, qualificationDto);
        return ResponseEntity.ok(qualificationDto);}

    // Delete a qualification
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
