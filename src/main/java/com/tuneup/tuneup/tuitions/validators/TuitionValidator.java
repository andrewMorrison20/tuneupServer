package com.tuneup.tuneup.tuitions.validators;

import com.tuneup.tuneup.tuitions.dtos.TuitionDto;
import com.tuneup.tuneup.tuitions.entities.Tuition;
import com.tuneup.tuneup.tuitions.repositories.TuitionRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class TuitionValidator {
    private final TuitionRepository tuitionRepository;

    public TuitionValidator(TuitionRepository tuitionRepository) {
        this.tuitionRepository = tuitionRepository;
    }

    /**
     * Validate a tuition
     * @param tuitionDto tuition to validaate
     */
    public void validateDto(TuitionDto tuitionDto){
       if(existsByTutorStudentId(tuitionDto.getTutorProfileId(),tuitionDto.getStudentProfileId())){
           throw new ValidationException("Tuition for these profiles already exists!");
       }
    }

    /**
     * Check if tuition exists for a given set of profiles
     * @param tutorId tutor profile of tuition
     * @param studentId student profile of tuition
     * @return Bool result
     */
    public boolean existsByTutorStudentId(Long tutorId, Long studentId) {
        return tuitionRepository.existsByTutorIdAndStudentId(tutorId,studentId);
    }

    /**
     * Fetch a  tuition by its id and validates exists
     * @param tuitionId id to fetch tuition for
     * @return tuition else throw
     */
    public Tuition fetchAndValidateById(Long tuitionId) {
        return tuitionRepository.findById(tuitionId)
                .orElseThrow(() -> new ValidationException("No existing tuition for id: " + tuitionId));
    }

    /**
     * Validate and fetch a tuition by its profile ids
     * @param studentProfileId
     * @param tutorProfileId
     * @return tuition else throw
     */
    public Tuition fetchAndValidateTuitionByIds(Long studentProfileId, Long tutorProfileId) {
        return tuitionRepository.findByStudentIdAndTutorId(studentProfileId, tutorProfileId)
                .orElseThrow(() -> new ValidationException("No tuition exists for the given student and tutor"));
    }

}
