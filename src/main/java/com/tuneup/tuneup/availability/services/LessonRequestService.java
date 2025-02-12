package com.tuneup.tuneup.availability.services;

import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.LessonRequest;
import com.tuneup.tuneup.availability.dtos.LessonDto;
import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.availability.enums.LessonRequestStatus;
import com.tuneup.tuneup.availability.mappers.AvailabilityMapper;
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
import java.util.Set;

@Service
public class LessonRequestService {

    private final LessonRequestRepository lessonRequestRepository;
    private final LessonRequestMapper lessonRequestMapper;
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final AvailabilityService availabilityService;
    private final LessonRequestValidator lessonRequestValidator;
    private final TuitionService tuitionService;
    private final AvailabilityMapper availabilityMapper;
    private final LessonService lessonService;

    public LessonRequestService(AvailabilityRepository availabilityRepository,
                                LessonRequestRepository lessonRequestRepository,
                                LessonRequestMapper lessonRequestMapper,
                                ProfileService profileService,
                                ProfileMapper profileMapper, AvailabilityService availabilityService, LessonRequestValidator lessonRequestValidator, TuitionService tuitionService, AvailabilityMapper availabilityMapper, LessonService lessonService) {
        this.lessonRequestRepository = lessonRequestRepository;
        this.lessonRequestMapper = lessonRequestMapper;
        this.profileService = profileService;
        this.profileMapper = profileMapper;
        this.availabilityService = availabilityService;
        this.lessonRequestValidator = lessonRequestValidator;
        this.tuitionService = tuitionService;
        this.availabilityMapper = availabilityMapper;
        this.lessonService = lessonService;
    }

    @Transactional
    public LessonRequestDto processLessonRequest(LessonRequestDto requestDto) {

        Availability availability = availabilityService.getAvailabilityByIdInternal(requestDto.getAvailabilityId());

        lessonRequestValidator.validateDuplicateRequest(requestDto.getStudentProfileId(),availability.getId());
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

        //TO-DO validate lesson request - lesson type
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
    public void updateLessonRequestStatus(Long lessonRequestId, String lessonReqStatus, Boolean autoDeclineConflicts) {
        LessonRequest request = getLessonRequestByIdInternal(lessonRequestId);
        Availability availability = request.getAvailability();
        LessonRequestStatus status = LessonRequestStatus.valueOf(lessonReqStatus);

        switch (status) {
            case CONFIRMED:
                handleConfirmedRequest(request, availability);

                TuitionDto tuitionDto = tuitionService.getTuitionByStudentAndTutor(request.getStudent().getId(),request.getTutor().getId());

                LessonDto lessonDto = new LessonDto();
                lessonDto.setTuitionId(tuitionDto.getId());
                lessonDto.setAvailabilityDto(availabilityMapper.toAvailabilityDto(availability));
                lessonDto.setLessonType(request.getLessonType());

                lessonService.createLesson(lessonDto);

                break;
            case DECLINED:
                handleDeclinedRequest(availability);
                break;
        }

        // Process conflicting requests before deleting the current request
        if (Boolean.TRUE.equals(autoDeclineConflicts)) {
            declineConflictingRequests(availability);
        }


        lessonRequestRepository.delete(request);
    }

    private void handleConfirmedRequest(LessonRequest request, Availability availability) {
        availabilityService.updateAvailabilityStatus(availability, AvailabilityStatus.BOOKED);

        // Check and create tuition if it doesn't exist
        if (!tuitionService.existsByProfileIds(request.getTutor().getId(), request.getStudent().getId())) {
            TuitionDto tuitionDto = new TuitionDto();
            tuitionDto.setActiveTuition(true);
            tuitionDto.setStartDate(LocalDate.now());
            tuitionDto.setStudentProfileId(request.getStudent().getId());
            tuitionDto.setTutorProfileId(request.getTutor().getId());
            tuitionService.createTuition(tuitionDto);
        }
    }

    private void handleDeclinedRequest(Availability availability) {
        availabilityService.updateAvailabilityStatus(availability, AvailabilityStatus.AVAILABLE);
    }

    private void declineConflictingRequests(Availability availability) {
        Set<LessonRequest> conflictingRequests = lessonRequestRepository.findAllByAvailabilityId(availability.getId());
        if (!conflictingRequests.isEmpty()) {
            lessonRequestRepository.deleteAll(conflictingRequests);
        }
    }


    /**
     * Fetch and validate an lessonRequest by id
     * note, should not be used in the controller layer as returns entity and not dto
     */
    public LessonRequest getLessonRequestByIdInternal(Long requestId){
        return lessonRequestValidator.fetchAndValidateById(requestId);
    }
}
