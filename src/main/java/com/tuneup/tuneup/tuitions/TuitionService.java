package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
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

    public Tuition getTuitionEntityByIdInternal(Long id) {
        return tuitionValidator.fetchAndValidateById(id);
    }

    public void deleteTuition(Long id) {
    }

    public Page<ProfileDto> getRequestsByProfile(Long profileId, Pageable pageable, boolean active) {
        Profile profile = profileService.fetchProfileEntityInternal(profileId);

        if (profile.getProfileType().equals(ProfileType.TUTOR)) {
            return getStudentsByTutor(profileId, pageable,active);
        } else {
            return getTutorsByStudent(profileId, pageable,active);
        }
    }

    public Page<ProfileDto> getStudentsByTutor(Long tutorId, Pageable pageable, boolean active) {
        profileService.existById(tutorId);
        return tuitionRepository.findStudentsByTutorId(tutorId, active ,pageable)
                .map(profileMapper::toProfileDto);
    }

    public Page<ProfileDto> getTutorsByStudent(Long studentId, Pageable pageable, boolean active) {
        profileService.existById(studentId);
        return tuitionRepository.findTutorsByStudentId(studentId,active, pageable)
                .map(profileMapper::toProfileDto);
    }

    public TuitionDto getTuitionByProfileIds(Long profileId, Long requesterProfileId) {
        Profile profile = profileService.fetchProfileEntityInternal(profileId);
        Profile requesterProfile = profileService.fetchProfileEntityInternal(requesterProfileId);

        TuitionDto tuitionDto = new TuitionDto();

        if(requesterProfile.getProfileType().equals(profile.getProfileType())){
            throw new ValidationException("Tuitions only exist between differing profile types. Cannot have tuition for two : " + profile.getProfileType());
        }

        if(requesterProfile.getProfileType().equals(ProfileType.STUDENT) && profile.getProfileType().equals(ProfileType.TUTOR)){
            tuitionDto =  tuitionMapper.toDto(tuitionValidator.fetchAndValidateTuitionByIds(requesterProfileId,profileId));
        }

        if(requesterProfile.getProfileType().equals(ProfileType.TUTOR) && profile.getProfileType().equals(ProfileType.STUDENT)){
            tuitionDto=  tuitionMapper.toDto(tuitionValidator.fetchAndValidateTuitionByIds(profileId,requesterProfileId));
        }
        return tuitionDto;

    }
}
