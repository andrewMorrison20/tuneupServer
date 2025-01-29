package com.tuneup.tuneup.availability.services;

import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.LessonRequest;
import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.availability.mappers.LessonRequestMapper;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.availability.repositories.LessonRequestRepository;
import com.tuneup.tuneup.profiles.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LessonRequestService {

    private final AvailabilityRepository availabilityRepository;
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
        
            this.availabilityRepository = availabilityRepository;
            this.lessonRequestRepository = lessonRequestRepository;
            this.lessonRequestMapper = lessonRequestMapper;
            this.profileService = profileService;
            this.profileMapper = profileMapper;
            this.availabilityService = availabilityService;
    }

    @Transactional
    public LessonRequestDto processLessonRequest(LessonRequestDto requestDto) {

        Availability availability = availabilityService.getAvailabilityByIdInternal(requestDto.getId());

        LocalDateTime requestStart = requestDto.getRequestedStartTime();
        LocalDateTime requestEnd = requestDto.getRequestedEndTime();

        // Check if the slot is still available before modifying
        if (!availability.getStatus().equals(AvailabilityStatus.AVAILABLE)) {
            throw new IllegalStateException("Slot is no longer available");
        }

        Availability pendingAvailability = new Availability();

        // Case 1: Full Slot Requested
        if (availability.getStartTime().equals(requestStart) && availability.getEndTime().equals(requestEnd)) {
            availability.setStatus(AvailabilityStatus.PENDING);
            pendingAvailability = availabilityRepository.save(availability);

        } else {
            //Case 2 : Partial slot requested

            pendingAvailability.setProfile(availability.getProfile());
            pendingAvailability.setStartTime(requestStart);
            pendingAvailability.setEndTime(requestEnd);
            pendingAvailability.setStatus(AvailabilityStatus.PENDING);

            pendingAvailability = availabilityRepository.save(pendingAvailability);

            if (availability.getStartTime().isBefore(requestStart) && availability.getEndTime().isAfter(requestEnd)) {

                Availability newAvailability = new Availability();

                newAvailability.setProfile(availability.getProfile());
                newAvailability.setStartTime(requestEnd);
                newAvailability.setEndTime(availability.getEndTime());
                newAvailability.setStatus(AvailabilityStatus.AVAILABLE);

                availabilityRepository.save(newAvailability);

                availability.setEndTime(requestStart);
                availabilityRepository.save(availability);
            } else if (availability.getStartTime().isBefore(requestStart)) {
                availability.setEndTime(requestStart);
                availabilityRepository.save(availability);
            } else if (availability.getEndTime().isAfter(requestEnd)) {
                availability.setStartTime(requestEnd);
                availabilityRepository.save(availability);
            }
        }

            LessonRequest lessonRequest = lessonRequestMapper.toLessonRequest(requestDto);
            lessonRequest.setStudent(profileMapper.toProfile(profileService.getProfileDto(requestDto.getStudentProfileId())));
            lessonRequest.setTutor(profileMapper.toProfile(profileService.getProfileDto(requestDto.getTutorProfileId())));
            lessonRequest.setAvailability(pendingAvailability);

            lessonRequest = lessonRequestRepository.save(lessonRequest);

            return lessonRequestMapper.toDto(lessonRequest);
        }
    }


