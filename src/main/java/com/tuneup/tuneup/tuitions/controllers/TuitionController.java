package com.tuneup.tuneup.tuitions.controllers;

import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.tuitions.TuitionDto;
import com.tuneup.tuneup.tuitions.services.TuitionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tuitions")
public class TuitionController {

    private final TuitionService tuitionService;

    public TuitionController(TuitionService tuitionService) {
        this.tuitionService = tuitionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TuitionDto> getTuitionById(@PathVariable Long id) {
        TuitionDto tuitionDto = tuitionService.getTuitionById(id);
        return ResponseEntity.ok(tuitionDto);
    }

    @GetMapping("/tuitionsByProfile/{profileId}")
    public ResponseEntity<Page<ProfileDto>> getTuitionsByProfile(
            @PathVariable Long profileId,
            @RequestParam boolean active,
            Pageable pageable) {

        Page<ProfileDto> tuitionProfileDtos = tuitionService.getRequestsByProfile(profileId, pageable,active);
        return ResponseEntity.ok(tuitionProfileDtos);
    }

    @GetMapping("/byStudentAndTutor")
    public ResponseEntity<TuitionDto> getTuitionByStudentAndTutor(
            @RequestParam Long studentProfileId,
            @RequestParam Long tutorProfileId) {
        TuitionDto tuitionDto = tuitionService.getTuitionByProfileIds(studentProfileId, tutorProfileId);
        return ResponseEntity.ok(tuitionDto);
    }

    @PostMapping
    public ResponseEntity<TuitionDto> createTuition(@RequestBody TuitionDto tuitionDto) {
        TuitionDto createdTuition = tuitionService.createTuition(tuitionDto);
        return ResponseEntity.ok(createdTuition);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TuitionDto> updateTuition(@PathVariable Long id, @RequestBody TuitionDto tuitionDto) {
        TuitionDto updatedTuition = tuitionService.updateTuition(id, tuitionDto);
        return ResponseEntity.ok(updatedTuition);
    }

@PatchMapping("/{id}/deactivate")
            public ResponseEntity<Void> deactivateTuition(@PathVariable Long id) {
        tuitionService.deactivateTuition(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTuition(@PathVariable Long id) {
        tuitionService.deleteTuition(id);
        return ResponseEntity.noContent().build();
    }
}
