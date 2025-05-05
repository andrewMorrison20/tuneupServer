package com.tuneup.tuneup.availability.controllers;

import com.tuneup.tuneup.availability.dtos.LessonDto;
import com.tuneup.tuneup.availability.dtos.LessonSummaryDto;
import com.tuneup.tuneup.availability.enums.LessonStatus;
import com.tuneup.tuneup.availability.services.LessonService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    /**
     *  Retrieve all lessons for a given tuition ID.
     */
    @GetMapping("/{tuitionId}")
    public ResponseEntity<Set<LessonDto>> getLessonsByTuition(@PathVariable Long tuitionId,
                                                              @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                              @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Set<LessonDto> lessons = lessonService.getLessonsByTuitionId(tuitionId,start,end);
        return ResponseEntity.ok(lessons);
    }

    /**
     * Cancel a lesson by ID and optionally reset availability.
     */
    @DeleteMapping("/cancel/{lessonId}")
    public ResponseEntity<Void> cancelLesson(
            @PathVariable Long lessonId,
            @RequestParam(value = "resetAvailability", defaultValue = "false") boolean resetAvailability) {

        lessonService.cancelLesson(lessonId, resetAvailability);
        return ResponseEntity.noContent().build();
    }


    /**
     * Update status by a lesson by ID.
     */
    @PatchMapping("/updateStatus/{lessonId}")
    public ResponseEntity<Void>updateLessonStatus(
            @PathVariable Long lessonId,
            @RequestParam(value = "lessonStatus") LessonStatus lessonStatus) {

        LessonDto lessonDto = lessonService.updateLessonStatus(lessonId, lessonStatus);
        return ResponseEntity.ok().build();
    }


    /**
     *  Retrieve all lessons for a given profile ID.
     */
    @GetMapping("/profileLessons/{profileId}")
    public ResponseEntity<Set<LessonDto>> getLessonsByProfile(@PathVariable Long profileId,
                                                              @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                              @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Set<LessonDto> lessons = lessonService.getLessonsByProfileId(profileId,start,end);
        return ResponseEntity.ok(lessons);
    }


    /**
     *  Retrieve lesson summary for a given Availability ID.
     */
    @GetMapping("/byAvailability/{availabilityId}")
    public ResponseEntity<LessonSummaryDto> getLessonsByAvailability(@PathVariable Long availabilityId){
        LessonSummaryDto summaryDto = lessonService.getLessonSummaryByAvailabilityId(availabilityId);
        return ResponseEntity.ok(summaryDto);
    }

    /**
     * Retrieve all outstanding lessons for a given tuition ID.
     */
    @GetMapping("/completed/{studentId}/{tutorId}")
    public ResponseEntity<Set<LessonDto>> getCompletedLessonsByTuition(
            @PathVariable Long studentId,
            @PathVariable Long tutorId) {
        Set<LessonDto> lessons = lessonService.getCompletedLessonsByTuitionId(studentId, tutorId);
        return ResponseEntity.ok(lessons);
    }
}
