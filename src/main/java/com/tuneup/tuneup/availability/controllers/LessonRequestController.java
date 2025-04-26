package com.tuneup.tuneup.availability.controllers;

import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.dtos.UpdateLessonRequestStatusDto;
import com.tuneup.tuneup.availability.services.LessonRequestService;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessonRequest")
public class LessonRequestController {

    private final LessonRequestService lessonRequestService;

    public LessonRequestController(LessonRequestService lessonRequestService) {
        this.lessonRequestService = lessonRequestService;
    }

    @PostMapping
    public ResponseEntity<LessonRequestDto> createLessonRequest(@RequestBody LessonRequestDto requestDto) {
        LessonRequestDto lessonRequestDto = lessonRequestService.processLessonRequest(requestDto);
        return ResponseEntity.status(201).body(lessonRequestDto);
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<Page<LessonRequestDto>> getLessonRequestsByTutor(
            @PathVariable Long tutorId,
            Pageable pageable) {

        Page<LessonRequestDto> lessonRequests = lessonRequestService.getRequestsByTutor(tutorId, pageable);
        return ResponseEntity.ok(lessonRequests);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<LessonRequestDto>> getLessonRequestsByStudent(
            @PathVariable Long studentId,
            Pageable pageable) {

        Page<LessonRequestDto> lessonRequests = lessonRequestService.getRequestsByStudent(studentId, pageable);
        return ResponseEntity.ok(lessonRequests);
    }

    @GetMapping
    public ResponseEntity<Page<LessonRequestDto>> getLessonRequestsByStudentAndTutor(
            @RequestParam Long studentId,
            @RequestParam Long tutorId,
            Pageable pageable) {

        Page<LessonRequestDto> lessonRequests = lessonRequestService.getTutorRequestsByStudent(studentId, tutorId, pageable);
        return ResponseEntity.ok(lessonRequests);
    }

    @GetMapping("/students/{tutorId}")
    public ResponseEntity<Page<ProfileDto>> getStudentsByTutor(
            @PathVariable Long tutorId, Pageable pageable) {

        return ResponseEntity.ok(lessonRequestService.getAllRequestProfilesByProfileId(tutorId, pageable));
    }

    @PatchMapping("/status/{lessonRequestId}")
    public ResponseEntity<Void> updateRequestStatus(
            @PathVariable Long lessonRequestId,
            @RequestBody UpdateLessonRequestStatusDto requestDto) {

        lessonRequestService.updateLessonRequestStatus(
                lessonRequestId, requestDto.getStatus(), requestDto.getAutoDeclineConflicts());

        return ResponseEntity.ok().build();
    }


    /**
     * delete a request by its id, this endpoint ismainly for testing since updating status triggers notificiation
     * Google throttles sign in attempts for gmail and so email service errors under extreme load.
     * @param lessonRequestId
     * @return
     */
    @DeleteMapping("/{lessonRequestId}")
    public ResponseEntity<Void> deleteLessonRequest(
            @PathVariable Long lessonRequestId) {

        lessonRequestService.deleteRequest(lessonRequestId);

        return ResponseEntity.ok().build();
    }
}
