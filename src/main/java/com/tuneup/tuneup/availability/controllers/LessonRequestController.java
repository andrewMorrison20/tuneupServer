package com.tuneup.tuneup.availability.controllers;

import com.tuneup.tuneup.availability.Lesson;
import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.services.LessonRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
    
    @GetMapping
    public ResponseEntity<Set<LessonRequestDto>> getLessonRequestsByStudentAndTutor(@RequestParam Long studentId,@RequestParam Long tutorId) {
        Set<LessonRequestDto> lessonRequests = lessonRequestService.getTutorRequestsByStudent(studentId,tutorId);
        return ResponseEntity.ok().body(lessonRequests);
    }
}
