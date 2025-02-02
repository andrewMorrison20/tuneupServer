package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
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
    private final ProfileMapper  profileMapper;


    public TuitionService(TuitionRepository tuitionRepository, ProfileService profileService, TuitionMapper tuitionMapper, TuitionValidator tuitionValidator, TuitionMapperImpl tuitionMapperImpl, ProfileMapper profileMapper) {
        this.tuitionRepository = tuitionRepository;
        this.profileService = profileService;
        this.tuitionMapper = tuitionMapper;
        this.tuitionValidator = tuitionValidator;
        this.profileMapper = profileMapper;
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

    public Page<ProfileDto> getRequestsByProfile(Long profileId, Pageable pageable) {
        Profile profile = profileService.fetchProfileEntityInternal(profileId);

        if (profile.getProfileType().equals(ProfileType.TUTOR)) {
            return getStudentsByTutor(profileId, pageable);
        } else {
            return getTutorsByStudent(profileId, pageable);
        }
    }

    public Page<ProfileDto> getStudentsByTutor(Long tutorId, Pageable pageable) {
        profileService.existById(tutorId);
        return tuitionRepository.findStudentsByTutorId(tutorId, pageable)
                .map(profileMapper::toProfileDto);
    }

    public Page<ProfileDto> getTutorsByStudent(Long studentId, Pageable pageable) {
        profileService.existById(studentId);
        return tuitionRepository.findTutorsByStudentId(studentId, pageable)
                .map(profileMapper::toProfileDto);
    }
}
