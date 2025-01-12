package com.tuneup.tuneup.qualifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public List<Qualification> getAllQualifications() {
        return qualificationService.getAllQualifications();
    }

    // Fetch a single qualification by ID
    @GetMapping("/{id}")
    public Optional<Qualification> getQualificationById(@PathVariable Long id) {
        return qualificationService.getQualificationById(id);
    }

    // Add a new qualification
    @PostMapping
    public Qualification addQualification(@RequestBody Qualification qualification) {
        return qualificationService.addQualification(qualification);
    }

    // Update an existing qualification
    @PutMapping("/{id}")
    public Qualification updateQualification(
            @PathVariable Long id,
            @RequestBody Qualification updatedQualification) {
        return qualificationService.updateQualification(id, updatedQualification);
    }

    // Delete a qualification
    @DeleteMapping("/{id}")
    public void deleteQualification(@PathVariable Long id) {
        qualificationService.deleteQualification(id);
    }
}
