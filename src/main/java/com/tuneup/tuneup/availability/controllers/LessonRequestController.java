package com.tuneup.tuneup.availability.controllers;

import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.dtos.UpdateLessonRequestStatusDto;
import com.tuneup.tuneup.availability.services.LessonRequestService;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This API is used to create and manage lesson requests.
 */
@RestController
@RequestMapping("/api/lessonRequest")
public class LessonRequestController {

    private final LessonRequestService lessonRequestService;

    public LessonRequestController(LessonRequestService lessonRequestService) {
        this.lessonRequestService = lessonRequestService;
    }

    /**
     * Create a new lesson request
     * @param requestDto the details of the request to create
     * @return LessonRequestDto - the newly create Lesson request
     */
    @PostMapping
    public ResponseEntity<LessonRequestDto> createLessonRequest(@RequestBody LessonRequestDto requestDto) {
        LessonRequestDto lessonRequestDto = lessonRequestService.processLessonRequest(requestDto);
        return ResponseEntity.status(201).body(lessonRequestDto);
    }

    /**
     * Retrieve all lesson requests that have been sent to a tutor
     * @param tutorId Id of the profile to retrieve lesson requests by
     * @param pageable
     * @return Page LessonRequestDtos
     */
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<Page<LessonRequestDto>> getLessonRequestsByTutor(
            @PathVariable Long tutorId,
            Pageable pageable) {

        Page<LessonRequestDto> lessonRequests = lessonRequestService.getRequestsByTutor(tutorId, pageable);
        return ResponseEntity.ok(lessonRequests);
    }

    /**
     * Retrieve all lessonRequests a student has sent.
     * @param studentId id of the student to retrieve requests for.
     * @param pageable
     * @return Page of LessonRequests for profile as DTOs
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<LessonRequestDto>> getLessonRequestsByStudent(
            @PathVariable Long studentId,
            Pageable pageable) {

        Page<LessonRequestDto> lessonRequests = lessonRequestService.getRequestsByStudent(studentId, pageable);
        return ResponseEntity.ok(lessonRequests);
    }

    /**
     * Retireve all lesson requests by the associated profiles.
     * @param studentId student involved in the request
     * @param tutorId tutor involved in the request
     * @param pageable details of the results to retrieve (size, page number)
     * @return Page LessonRequest as Dtos
     */
    @GetMapping
    public ResponseEntity<Page<LessonRequestDto>> getLessonRequestsByStudentAndTutor(
            @RequestParam Long studentId,
            @RequestParam Long tutorId,
            Pageable pageable) {

        Page<LessonRequestDto> lessonRequests = lessonRequestService.getTutorRequestsByStudent(studentId, tutorId, pageable);
        return ResponseEntity.ok(lessonRequests);
    }

    /**
     * Find all students that have sent requests to a given tutor.
     * @param tutorId the id of the tutor to find student for
     * @param pageable size of results set to retrieve
     * @return Page ProfileDtos
     */
    @GetMapping("/students/{tutorId}")
    public ResponseEntity<Page<ProfileDto>> getStudentsByTutor(
            @PathVariable Long tutorId, Pageable pageable) {

        return ResponseEntity.ok(lessonRequestService.getAllRequestProfilesByProfileId(tutorId, pageable));
    }

    /**
     * Update the status of a given lesson request.
     * @param lessonRequestId the id of the lesson request to update
     * @param requestDto the updated request
     * @return success status
     */
    @PatchMapping("/status/{lessonRequestId}")
    public ResponseEntity<Void> updateRequestStatus(
            @PathVariable Long lessonRequestId,
            @RequestBody UpdateLessonRequestStatusDto requestDto) {

        lessonRequestService.updateLessonRequestStatus(
                lessonRequestId, requestDto.getStatus(), requestDto.getAutoDeclineConflicts());

        return ResponseEntity.ok().build();
    }


    /**
     * delete a request by its id, Primarily an admin use case, delete spam requests if a user has been overwhelmed by request
     * and cannot perform the admin task themselves.
     *
     * @param lessonRequestId
     * @return success status
     */
    @DeleteMapping("/{lessonRequestId}")
    public ResponseEntity<Void> deleteLessonRequest(
            @PathVariable Long lessonRequestId) {

        lessonRequestService.deleteRequest(lessonRequestId);

        return ResponseEntity.ok().build();
    }
}
