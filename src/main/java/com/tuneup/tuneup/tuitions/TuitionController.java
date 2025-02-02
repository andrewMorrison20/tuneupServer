package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    @GetMapping("/students/{tutorId}")
    public ResponseEntity<Page<TuitionDto>> getTuitionsByTutor(
            @PathVariable Long tutorId,
            Pageable pageable) {

        Page<TuitionDto>tuitionDtos = tuitionService.getRequestsByTutor(tutorId, pageable);
        return ResponseEntity.ok(tuitionDtos);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTuition(@PathVariable Long id) {
        tuitionService.deleteTuition(id);
        return ResponseEntity.noContent().build();
    }
}
