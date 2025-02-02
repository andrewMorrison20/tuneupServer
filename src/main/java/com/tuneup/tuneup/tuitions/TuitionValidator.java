package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TuitionValidator {
    private final TuitionRepository tuitionRepository;

    public TuitionValidator(TuitionRepository tuitionRepository) {
        this.tuitionRepository = tuitionRepository;
    }

    public void validateDto(TuitionDto tuitionDto){
       List<ValidationException> errors = new ArrayList<>();
    }

    public boolean existsByTutorStudentId(Long tutorId, Long studentId) {
        return tuitionRepository.existsByTutorIdAndStudentId(tutorId,studentId);
    }
}
