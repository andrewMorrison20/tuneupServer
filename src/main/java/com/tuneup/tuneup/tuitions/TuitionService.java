package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.mappers.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    /**
     * Create a tuition between two profiles
     * @param tuitionDto summary of the tuition to create
     * @return dto of the saved tuition
     */
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

    /**
     * checks a tuiution exists between two profiles
     * @param tutorId tutor
     * @param studentId student
     * @return boolean if exists
     */
    public boolean existsByProfileIds(Long tutorId, Long studentId){
        return tuitionValidator.existsByTutorStudentId(tutorId,studentId);
    }

    /**
     * gets a  tuiution exists between two profiles
     * @param tutorId tutor
     * @param studentId student
     * @return tuition if exists
     */
    public Tuition getByProfileIds(Long tutorId, Long studentId){
        return tuitionRepository.findByStudentIdAndTutorId(studentId,tutorId).orElseThrow(
                ()-> new ValidationException("No existing tuition"));
    }
    //TO-DO implement
    public TuitionDto updateTuition(Long id, TuitionDto tuitionDto) {
        return null;
    }

    /**
     * Fetch a tuition from db by its id
     * @param id id of the tuition to fetch
     * @return tuition entity from db
     */
    public TuitionDto getTuitionById(Long id) {
       return tuitionMapper.toDto(tuitionValidator.fetchAndValidateById(id));
    }

    /**
     * Returns entity instead of dto, only for internal use, should not be used in repsonses at controller layer
     * @param id id of tuition to fetch
     * @return tuition retrieved from db
     */
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

    /**
     * Get all Students linked to a given tutor via a tuition
     * @param tutorId profile id of the tutor to fetch for
     * @param pageable page to return
     * @param active only active tuitions
     * @return page of student profiledtos currently in active tuition with tutor
     */
    public Page<ProfileDto> getStudentsByTutor(Long tutorId, Pageable pageable, boolean active) {
        profileService.existById(tutorId);
        return tuitionRepository.findStudentsByTutorId(tutorId, active ,pageable)
                .map(profileMapper::toProfileDto);
    }

    /**
     * Get all tutors linkeed to a given student via a tuition
     * @param studentId profile id of the student
     * @param pageable page to return
     * @param active only active tuitions
     * @return page of tutor profiledtos currently in active tuition with student
     */
    public Page<ProfileDto> getTutorsByStudent(Long studentId, Pageable pageable, boolean active) {
        profileService.existById(studentId);
        return tuitionRepository.findTutorsByStudentId(studentId,active, pageable)
                .map(profileMapper::toProfileDto);
    }

    /**
     * Fetch a tuition linked to two given profiles
     * @param profileId the id of the profile to fetch tuition for
     * @param requesterProfileId the id of the profile requesting the data
     * @return a tuition dto
     */

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

    /**
     * Deactivate a tuition
     * @param id the id of the tuition to deactivate
     */
    public void deactivateTuition(Long id) {
        Tuition tuition = findById(id);
        tuition.setActiveTuition(false);
        tuitionRepository.save(tuition);
    }

    /**
     * reactivate a tuition
     * @param id the id of the tuition to reactivate
     */
    public void reactivateTuition(Long id) {
        Tuition tuition = findById(id);
        tuition.setActiveTuition(true);
        tuitionRepository.save(tuition);
    }

    /**
     * Fetch a given tuition from the db by id
     * @param tuitionId id of the tuition to fetch
     * @return tuition entity from db
     */
    public Tuition findById(Long tuitionId) {
        return tuitionRepository.findById(tuitionId)
                .orElseThrow(() -> new ValidationException("No tuition found for id: " + tuitionId));
    }
}
