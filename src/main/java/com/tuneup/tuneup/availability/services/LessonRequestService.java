package com.tuneup.tuneup.availability.services;

import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.LessonRequest;
import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.availability.enums.LessonRequestStatus;
import com.tuneup.tuneup.availability.mappers.LessonRequestMapper;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.availability.repositories.LessonRequestRepository;
import com.tuneup.tuneup.availability.validators.LessonRequestValidator;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.tuitions.TuitionDto;
import com.tuneup.tuneup.tuitions.TuitionService;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LessonRequestService {

    private final LessonRequestRepository lessonRequestRepository;
    private final LessonRequestMapper lessonRequestMapper;
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final AvailabilityService availabilityService;
    private final LessonRequestValidator lessonRequestValidator;
    private final TuitionService tuitionService;

    public LessonRequestService(AvailabilityRepository availabilityRepository,
                                LessonRequestRepository lessonRequestRepository,
                                LessonRequestMapper lessonRequestMapper,
                                ProfileService profileService,
                                ProfileMapper profileMapper, AvailabilityService availabilityService, LessonRequestValidator lessonRequestValidator, TuitionService tuitionService) {
        this.lessonRequestRepository = lessonRequestRepository;
        this.lessonRequestMapper = lessonRequestMapper;
        this.profileService = profileService;
        this.profileMapper = profileMapper;
        this.availabilityService = availabilityService;
        this.lessonRequestValidator = lessonRequestValidator;
        this.tuitionService = tuitionService;
    }

    @Transactional
    public LessonRequestDto processLessonRequest(LessonRequestDto requestDto) {

        Availability availability = availabilityService.getAvailabilityByIdInternal(requestDto.getAvailabilityId());

        availabilityService.validateAvailability(availability);

        LocalDateTime requestStart = requestDto.getRequestedStartTime();
        LocalDateTime requestEnd = requestDto.getRequestedEndTime();

        Availability pendingAvailability = availabilityService.handleAvailabilityAdjustment(availability, requestStart, requestEnd);

        return createLessonRequest(requestDto, pendingAvailability);
    }



    /**
     * Creates and saves a new lesson request
     * @
     */
    public LessonRequestDto createLessonRequest(LessonRequestDto requestDto, Availability pendingAvailability) {
        LessonRequest lessonRequest = lessonRequestMapper.toLessonRequest(requestDto);
        lessonRequest.setStudent(profileMapper.toProfile(profileService.getProfileDto(requestDto.getStudentProfileId())));
        lessonRequest.setTutor(profileMapper.toProfile(profileService.getProfileDto(requestDto.getTutorProfileId())));
        lessonRequest.setAvailability(pendingAvailability);

        lessonRequest = lessonRequestRepository.save(lessonRequest);

        return lessonRequestMapper.toDto(lessonRequest);
    }

    /**
     * Returns the set of pending lesson requests for a given student and tutor
     * @return set of dto requests - lessonDtos
     */
    public Page<LessonRequestDto> getTutorRequestsByStudent(Long studentId, Long tutorId,Pageable pageable){

        if (!profileService.existById(studentId) || !profileService.existById(tutorId)){
            throw new ValidationException("Invalid combination of profile ids provided");
        }

        return lessonRequestRepository.findRequestsByTutorIdAndStudentId(studentId,tutorId,pageable)
                .map(lessonRequestMapper::toDto);
    }

    /**
     * gets the set of existing lesson requests a particular tutor (from all students).
     * @param tutorId
     * @param pageable
     * @return Page lessonRequestDto
     */
    public Page<LessonRequestDto> getRequestsByTutor(Long tutorId, Pageable pageable) {
        profileService.existById(tutorId);
        return lessonRequestRepository.findByTutorId(tutorId, pageable)
                .map(lessonRequestMapper::toDto);
    }

    /**
     * gets the set of existing lesson requests a particular student.
     * @param studentId
     * @param pageable
     * @return Page lessonRequestDto
     */
    public Page<LessonRequestDto> getRequestsByStudent(Long studentId, Pageable pageable) {
        profileService.existById(studentId);
        return lessonRequestRepository.findByStudentId(studentId, pageable)
                .map(lessonRequestMapper::toDto);
    }

    public Page<ProfileDto> getAllRequestProfilesByProfileId(Long profileId,Pageable pageable){
        Profile profile = profileService.fetchProfileEntityInternal(profileId);
        if(profile.getProfileType().equals(ProfileType.TUTOR)){
            return getStudentsByTutor(profileId,pageable);
        } else if(profile.getProfileType().equals(ProfileType.STUDENT)){
            return getTutorsByStudent(profileId,pageable);
        }
        return Page.empty();
    }
    /**
     * gets the set of distinct profiles that have sent lesson requests to a given tutor
     * @param tutorId
     * @param pageable
     * @return Page o profile dtos
     */
    private Page<ProfileDto> getStudentsByTutor(Long tutorId, Pageable pageable) {
        return lessonRequestRepository.findStudentsByTutorId(tutorId, pageable)
                .map(profileMapper::toProfileDto);
    }

    /**
     * gets the set of distinct profiles that a student has sent requests to
     * @param studentId
     * @param pageable
     * @return Page o profile dtos
     */
    private Page<ProfileDto> getTutorsByStudent(Long studentId, Pageable pageable) {
        return lessonRequestRepository.findTutorsByStudentId(studentId, pageable)
                .map(profileMapper::toProfileDto);
    }

    @Transactional
    public void updateLessonRequestStatus(Long lessonRequestId, String lessonReqStatus) {

        LessonRequest request = getLessonRequestByIdInternal(lessonRequestId);
        LessonRequestStatus status = LessonRequestStatus.valueOf(lessonReqStatus);

        if(status.equals(LessonRequestStatus.CONFIRMED)) {

            //update corresponding time slot to booked
            Availability availability = request.getAvailability();
            availabilityService.updateAvailabilityStatus(availability, AvailabilityStatus.BOOKED);

            //check if there is an existing tuition between student and tutor - if not, create it
            if(!tuitionService.existsByProfileIds(request.getTutor().getId(),request.getStudent().getId())) {

                TuitionDto tuitionDto = new TuitionDto();
                tuitionDto.setActiveTuition(true);
                tuitionDto.setStartDate(LocalDate.now());
                tuitionDto.setStudentProfileId(request.getStudent().getId());
                tuitionDto.setTutorProfileId(request.getTutor().getId());
                tuitionService.createTuition(tuitionDto);
            }

        } else if (status.equals(LessonRequestStatus.DECLINED)){
            Availability availability = request.getAvailability();
            availabilityService.updateAvailabilityStatus(availability, AvailabilityStatus.AVAILABLE);
        }

        lessonRequestRepository.delete(request);
    }

    /**
     * Fetch and validate an lessonRequest by id
     * note, should not be used in the controller layer as returns entity and not dto
     */
    public LessonRequest getLessonRequestByIdInternal(Long requestId){
        return lessonRequestValidator.fetchAndValidateById(requestId);
    }
}
