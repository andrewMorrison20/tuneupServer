package com.tuneup.tuneup.availability.services;

import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.LessonRequest;
import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.mappers.LessonRequestMapper;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.availability.repositories.LessonRequestRepository;
import com.tuneup.tuneup.profiles.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LessonRequestService {

    private final LessonRequestRepository lessonRequestRepository;
    private final LessonRequestMapper lessonRequestMapper;
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final AvailabilityService availabilityService;

    public LessonRequestService(AvailabilityRepository availabilityRepository,
                                LessonRequestRepository lessonRequestRepository,
                                LessonRequestMapper lessonRequestMapper,
                                ProfileService profileService,
                                ProfileMapper profileMapper, AvailabilityService availabilityService) {
        this.lessonRequestRepository = lessonRequestRepository;
        this.lessonRequestMapper = lessonRequestMapper;
        this.profileService = profileService;
        this.profileMapper = profileMapper;
        this.availabilityService = availabilityService;
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
    public Set<LessonRequestDto> getTutorRequestsByStudent(Long studentId, Long tutorId){

        if (!profileService.existById(studentId) || !profileService.existById(tutorId)){
            throw new ValidationException("Invalid combination of profile ids provided");
        }

        return lessonRequestRepository.findByStudentIdAndTutorId(studentId, tutorId).stream()
                .map(lessonRequestMapper::toDto)
                .collect(Collectors.toSet());


    };
}
