package com.tuneup.tuneup.availability.validators;

import com.tuneup.tuneup.availability.LessonRequest;
import com.tuneup.tuneup.availability.repositories.LessonRequestRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class LessonRequestValidator {

    private final LessonRequestRepository lessonRequestRepository;

    public LessonRequestValidator(LessonRequestRepository lessonRequestRepository) {
        this.lessonRequestRepository = lessonRequestRepository;
    }


    public LessonRequest fetchAndValidateById(Long id) {
        return lessonRequestRepository.findById(id).orElseThrow(
                () -> new ValidationException("Lesson request not found for id: " + id)
        );
    }
}
