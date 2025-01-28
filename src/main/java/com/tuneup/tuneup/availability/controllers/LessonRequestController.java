package com.tuneup.tuneup.availability.controllers;

import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.services.LessonRequestService;
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
}
