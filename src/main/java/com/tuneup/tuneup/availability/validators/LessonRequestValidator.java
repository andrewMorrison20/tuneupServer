package com.tuneup.tuneup.availability.validators;

import com.tuneup.tuneup.availability.entities.LessonRequest;
import com.tuneup.tuneup.availability.repositories.LessonRequestRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class LessonRequestValidator {

    private final LessonRequestRepository lessonRequestRepository;

    public LessonRequestValidator(LessonRequestRepository lessonRequestRepository) {
        this.lessonRequestRepository = lessonRequestRepository;
    }

    /**
     * Checks for duplicate entries prior to creating request.
     * @param studentId the id of the student profile making the request
     * @param availabilityId the slot being booked against
     */
    public void validateDuplicateRequest(Long studentId, Long availabilityId) {
        if (lessonRequestRepository.existsByStudentIdAndAvailabilityId(studentId, availabilityId)) {
            throw new ValidationException("Request already exists for Student and availability: " + availabilityId);
        }
    }

    /**
     * Retrieves lesson request if it exists, else throws. This is carried out here instead of at service layer to centralise validation exception handling
     * @param id the id of the slot to fetch
     * @return the LessonRequest or throw
     */
    public LessonRequest fetchAndValidateById(Long id) {
        return lessonRequestRepository.findById(id).orElseThrow(
                () -> new ValidationException("Lesson request not found for id: " + id)
        );
    }
}
