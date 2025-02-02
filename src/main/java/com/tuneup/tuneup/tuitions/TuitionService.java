package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TuitionService {

    private final TuitionRepository tuitionRepository;
    private final ProfileService profileService;
    private final TuitionMapper tuitionMapper;
    private final  TuitionValidator tuitionValidator;
    private final TuitionMapperImpl tuitionMapperImpl;


    public TuitionService(TuitionRepository tuitionRepository, ProfileService profileService, TuitionMapper tuitionMapper, TuitionValidator tuitionValidator, TuitionMapperImpl tuitionMapperImpl) {
        this.tuitionRepository = tuitionRepository;
        this.profileService = profileService;
        this.tuitionMapper = tuitionMapper;
        this.tuitionValidator = tuitionValidator;
        this.tuitionMapperImpl = tuitionMapperImpl;
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

    //TO-DO implement
    public TuitionDto updateTuition(Long id, TuitionDto tuitionDto) {
        return null;
    }

    public TuitionDto getTuitionById(Long id) {
       return tuitionMapper.toDto(tuitionValidator.fetchAndValidateById(id));
    }

    public void deleteTuition(Long id) {
    }

    public Page<TuitionDto> getRequestsByTutor(Long tutorId, Pageable pageable) {

        profileService.existById(tutorId);

        Page<Tuition> allTuitions = tuitionRepository.findAllByTutorId(tutorId, pageable);

        return allTuitions.map(tuitionMapper::toDto);
    }
}
