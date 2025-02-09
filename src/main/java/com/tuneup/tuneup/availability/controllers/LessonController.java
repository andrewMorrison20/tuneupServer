package com.tuneup.tuneup.availability.controllers;

import com.tuneup.tuneup.availability.dtos.LessonDto;
import com.tuneup.tuneup.availability.services.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    /**
     * 🔹 Retrieve all lessons for a given tuition ID.
     */
    @GetMapping("/{tuitionId}")
    public ResponseEntity<Set<LessonDto>> getLessonsByTuition(@PathVariable Long tuitionId) {
        Set<LessonDto> lessons = lessonService.getLessonsByTuitionId(tuitionId);
        return ResponseEntity.ok(lessons);
    }

    /**
     * 🔹 Cancel a lesson by ID.
     */
    @DeleteMapping("/{lessonId}/cancel")
    public ResponseEntity<Void> cancelLesson(@PathVariable Long lessonId) {
        lessonService.cancelLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}
