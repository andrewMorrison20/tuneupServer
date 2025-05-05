package com.tuneup.tuneup.tuitions.controllers;

import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.tuitions.dtos.TuitionDto;
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

    /**
     * REtrieve a tuition by its associated id
     * @param id id of the tuition to fetch
     * @return tuition entity
     */
    @GetMapping("/{id}")
    public ResponseEntity<TuitionDto> getTuitionById(@PathVariable Long id) {
        TuitionDto tuitionDto = tuitionService.getTuitionById(id);
        return ResponseEntity.ok(tuitionDto);
    }

    /**
     * Retireve all tuitions for an associated profile
     * @param profileId id of profile to fetch tuitions for
     * @param active status of the tuition
     * @param pageable
     * @return associated tuitions
     */
    @GetMapping("/tuitionsByProfile/{profileId}")
    public ResponseEntity<Page<ProfileDto>> getTuitionsByProfile(
            @PathVariable Long profileId,
            @RequestParam boolean active,
            Pageable pageable) {

        Page<ProfileDto> tuitionProfileDtos = tuitionService.getRequestsByProfile(profileId, pageable,active);
        return ResponseEntity.ok(tuitionProfileDtos);
    }

    /**
     * Fetch the tuition between two profiles by their ids
     * @param studentProfileId student profile id of the tuition
     * @param tutorProfileId tutor profile of the tuition
     * @return tuition
     */
    @GetMapping("/byStudentAndTutor")
    public ResponseEntity<TuitionDto> getTuitionByStudentAndTutor(
            @RequestParam Long studentProfileId,
            @RequestParam Long tutorProfileId) {
        TuitionDto tuitionDto = tuitionService.getTuitionByProfileIds(studentProfileId, tutorProfileId);
        return ResponseEntity.ok(tuitionDto);
    }

    /**
     * Create a new tuition for given profiles
     * @param tuitionDto tuition to create
     * @return newly created tuition
     */
    @PostMapping
    public ResponseEntity<TuitionDto> createTuition(@RequestBody TuitionDto tuitionDto) {
        TuitionDto createdTuition = tuitionService.createTuition(tuitionDto);
        return ResponseEntity.ok(createdTuition);
    }

    /**
     * Update an existing tuition
     * @param id id of the tuition to update
     * @param tuitionDto uopdated fields
     * @return the updated tuition
     */
    @PutMapping("/{id}")
    public ResponseEntity<TuitionDto> updateTuition(@PathVariable Long id, @RequestBody TuitionDto tuitionDto) {
        TuitionDto updatedTuition = tuitionService.updateTuition(id, tuitionDto);
        return ResponseEntity.ok(updatedTuition);
    }

    /**
     * Deactivate a given tuition
     * @param id id of the tuition to deactivate
     * @return success status
     */
    @PatchMapping("/{id}/deactivate")
            public ResponseEntity<Void> deactivateTuition(@PathVariable Long id) {
        tuitionService.deactivateTuition(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a tuition
     * @param id of tuition to delete
     * @return success status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTuition(@PathVariable Long id) {
        tuitionService.deleteTuition(id);
        return ResponseEntity.noContent().build();
    }
}
