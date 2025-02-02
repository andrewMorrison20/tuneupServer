package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class TuitionService {

    private final TuitionRepository tuitionRepository;
    private final ProfileService profileService;
    private final TuitionMapper tuitionMapper;
    private final  TuitionValidator tuitionValidator;


    public TuitionService(TuitionRepository tuitionRepository, ProfileService profileService, TuitionMapper tuitionMapper, TuitionValidator tuitionValidator) {
        this.tuitionRepository = tuitionRepository;
        this.profileService = profileService;
        this.tuitionMapper = tuitionMapper;
        this.tuitionValidator = tuitionValidator;
    }

    public TuitionDto createTuition(TuitionDto tuitionDto){

        //Fetch and validate profile ids
        Profile student = profileService.fetchProfileEntityInternal(tuitionDto.getStudentProfileId());
        Profile tutor = profileService.fetchProfileEntityInternal(tuitionDto.getTutorProfileId());

        //Validate vars in tuition dto
        tuitionValidator.validateDto(tuitionDto);
        Tuition tuition = tuitionMapper.toEntity(tuitionDto);

        //manually set  tuition profile objects since exluded from mapping
        tuition.setStudent(student);
        tuition.setTutor(tutor);
        tuition = tuitionRepository.save(tuition);

        //Return as dto to avoid mapping in controller layer
        return tuitionMapper.toDto(tuition);
    }

    public boolean existsByProfileIds(Long tutorId, Long studentId){
        return tuitionValidator.existsByTutorStudentId(tutorId,studentId);
    }
}
