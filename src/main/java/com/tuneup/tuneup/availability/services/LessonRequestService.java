package com.tuneup.tuneup.availability.services;

import com.tuneup.tuneup.availability.entities.Availability;
import com.tuneup.tuneup.availability.entities.LessonRequest;
import com.tuneup.tuneup.availability.dtos.LessonDto;
import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.availability.enums.LessonRequestStatus;
import com.tuneup.tuneup.availability.mappers.AvailabilityMapper;
import com.tuneup.tuneup.availability.mappers.LessonRequestMapper;
import com.tuneup.tuneup.availability.repositories.LessonRequestRepository;
import com.tuneup.tuneup.availability.validators.LessonRequestValidator;
import com.tuneup.tuneup.notifications.NotificationEvent;
import com.tuneup.tuneup.notifications.enums.NotificationType;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.mappers.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.tuitions.dtos.TuitionDto;
import com.tuneup.tuneup.tuitions.services.TuitionService;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    public LessonRequestService(LessonRequestRepository lessonRequestRepository,
                                LessonRequestMapper lessonRequestMapper,
                                ProfileService profileService,
                                ProfileMapper profileMapper,
                                AvailabilityService availabilityService,
                                LessonRequestValidator lessonRequestValidator,
                                TuitionService tuitionService,
                                AvailabilityMapper availabilityMapper,
                                LessonService lessonService,
                                ApplicationEventPublisher eventPublisher) {
        this.lessonRequestRepository = lessonRequestRepository;
        this.lessonRequestMapper = lessonRequestMapper;
        this.profileService = profileService;
        this.profileMapper = profileMapper;
        this.availabilityService = availabilityService;
        this.lessonRequestValidator = lessonRequestValidator;
        this.tuitionService = tuitionService;
        this.availabilityMapper = availabilityMapper;
        this.lessonService = lessonService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Processes an incoming lesson request and adjusts the associated availability slot.
     * @param requestDto the details of the request
     * @return The created lesson as a DTO
     */
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
     * Creates a new lesson request against an existing availability slot.
     * @param requestDto the request to create
     * @param pendingAvailability the availability slot to book against
     * @return LessonRequest- the newly created request
     */
    public LessonRequestDto createLessonRequest(LessonRequestDto requestDto, Availability pendingAvailability) {

        LessonRequest lessonRequest = lessonRequestMapper.toLessonRequest(requestDto);
        lessonRequest.setStudent(profileService.fetchProfileEntityInternal(requestDto.getStudentProfileId()));
        lessonRequest.setTutor(profileService.fetchProfileEntityInternal(requestDto.getTutorProfileId()));
        lessonRequest.setAvailability(pendingAvailability);

        lessonRequest = lessonRequestRepository.save(lessonRequest);

        //Law of demeter well and truly broken here.
        Long userId = lessonRequest.getTutor().getAppUser().getId();

        eventPublisher.publishEvent(
                new NotificationEvent(this, userId, NotificationType.LESSON_REQUEST, "You have a new lesson Request from  " + lessonRequest.getStudent().getDisplayName())
        );

        return lessonRequestMapper.toDto(lessonRequest);
    }

    /**
     * Returns the set of pending lesson requests for a given student and tutor
     * @return set of dto requests - lessonDtos
     */
    public Page<LessonRequestDto> getTutorRequestsByStudent(Long requesterId, Long profileId,Pageable pageable){

        Profile requesterProfile = profileService.fetchProfileEntityInternal(requesterId);
        Profile profile = profileService.fetchProfileEntityInternal(profileId);

        Page<LessonRequestDto> requests = null;

        if(requesterProfile.getProfileType().equals(ProfileType.STUDENT) && profile.getProfileType().equals(ProfileType.TUTOR)){
            requests = lessonRequestRepository.findRequestsByTutorIdAndStudentId(requesterId,profileId,pageable)
                    .map(lessonRequestMapper::toDto);
        }

        if(requesterProfile.getProfileType().equals(ProfileType.TUTOR) && profile.getProfileType().equals(ProfileType.STUDENT)){
            requests = lessonRequestRepository.findRequestsByTutorIdAndStudentId(profileId,requesterId,pageable)
                    .map(lessonRequestMapper::toDto);
        }


        return requests;
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

    /**
     * Get the full set of profiles that have sent/received lesson request for the send/receiver id
     * @param profileId the id to find profiles by
     * @param pageable
     * @return Page ProfileDto - the set of profiles related to the search profile
     */
    public Page<ProfileDto> getAllRequestProfilesByProfileId(Long profileId, Pageable pageable) {
        Profile profile = profileService.fetchProfileEntityInternal(profileId);

        ProfileType type = profile != null ? profile.getProfileType() : null;

        if (ProfileType.TUTOR.equals(type)) {
            return getStudentsByTutor(profileId, pageable);
        } else if (ProfileType.STUDENT.equals(type)) {
            return getTutorsByStudent(profileId, pageable);
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

    /**
     * Update the stauts of an existing lesson Request and create a lesson if required
     * @param lessonRequestId the id of the lesson request to update
     * @param lessonReqStatus the status to update to
     * @param autoDeclineConflicts - automatically delete/decline any other request that matches this criteria
     */
    @Transactional
    public void updateLessonRequestStatus(Long lessonRequestId, String lessonReqStatus, Boolean autoDeclineConflicts) {
        LessonRequest request = getLessonRequestByIdInternal(lessonRequestId);
        Availability availability = request.getAvailability();
        LessonRequestStatus status = LessonRequestStatus.valueOf(lessonReqStatus);
        NotificationType type = null;

        switch (status) {
            case CONFIRMED:
                handleConfirmedRequest(request, availability);

                TuitionDto tuitionDto = tuitionService.getTuitionByProfileIds(request.getStudent().getId(),request.getTutor().getId());

                //check if tuition is inactive, update to active if so
                if(!tuitionDto.isActiveTuition()){
                    tuitionService.reactivateTuition(tuitionDto.getId());
                }

                LessonDto lessonDto = new LessonDto();
                lessonDto.setTuitionId(tuitionDto.getId());
                lessonDto.setAvailabilityDto(availabilityMapper.toAvailabilityDto(availability));
                lessonDto.setLessonType(request.getLessonType());

                lessonService.createLesson(lessonDto);
                type = NotificationType.REQUEST_ACCEPTED;

                break;
            case DECLINED:
                handleDeclinedRequest(availability);
                type = NotificationType.REQUEST_REJECTED;
                break;
        }

        // Process conflicting requests before deleting the current request
        if (Boolean.TRUE.equals(autoDeclineConflicts)) {
            declineConflictingRequests(availability);
        }

        eventPublisher.publishEvent(
                new NotificationEvent(this, request.getStudent().getAppUser().getId(), type, "Your lesson Request with : " +
                        request.getTutor().getDisplayName()+
                        "has been updated : " + type.getDisplayName())
        );

        lessonRequestRepository.delete(request);
    }

    /**
     * Handles the followup adjustments to availability when a lesson request is updated
     * @param request the request being updated
     * @param availability the associated slot
     */
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

    /**
     * Handle the decline of requests, set availability back to available
     * @param availability the slot to
     */
    private void handleDeclinedRequest(Availability availability) {
        availabilityService.updateAvailabilityStatus(availability, AvailabilityStatus.AVAILABLE);
    }

    /**
     * Reject any other requests for the same availability slot
     * @param availability the availability slot to update status for.
     */
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

    /**
     * Primarily used for e2e tests
     * @param lessonRequestId
     */
    public void deleteRequest(Long lessonRequestId) {
        lessonRequestRepository.deleteById(lessonRequestId);
    }
}
