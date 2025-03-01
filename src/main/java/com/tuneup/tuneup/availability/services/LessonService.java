package com.tuneup.tuneup.availability.services;


import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.Lesson;
import com.tuneup.tuneup.availability.dtos.LessonDto;
import com.tuneup.tuneup.availability.dtos.LessonSummaryDto;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.availability.enums.LessonStatus;
import com.tuneup.tuneup.availability.mappers.LessonMapper;
import com.tuneup.tuneup.availability.repositories.LessonRepository;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.tuitions.TuitionRepository;
import com.tuneup.tuneup.tuitions.TuitionService;
import com.tuneup.tuneup.tuitions.TuitionValidator;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private final LessonMapper lessonMapper;
    private final LessonRepository lessonRepository;
    private final TuitionRepository tuitionRepository;
    private final TuitionService tuitionService;
    private final ProfileService profileService;
    private final AvailabilityService availabilityService;

    public LessonService(LessonMapper lessonMapper,
                         LessonRepository lessonRepository,
                         TuitionValidator tuitionValidator,
                         TuitionRepository tuitionRepository,
                         TuitionService tuitionService,
                         ProfileService profileService, AvailabilityService availabilityService) {
        this.lessonMapper = lessonMapper;
        this.lessonRepository = lessonRepository;
        this.tuitionRepository = tuitionRepository;
        this.tuitionService = tuitionService;
        this.profileService = profileService;
        this.availabilityService = availabilityService;
    }

    /**
     * Create a new lesson post lesson request approval/confirmation
     * @param lessonDto
     */
    public void createLesson(LessonDto lessonDto) {

        Lesson lesson =  lessonMapper.toEntity(lessonDto);
        lesson.setLessonStatus(LessonStatus.CONFIRMED);
        lesson.setTuition(tuitionService.getTuitionEntityByIdInternal(lessonDto.getTuitionId()));
        lessonRepository.save(lesson);
    }

    /**
     * Get all lessons for a particular tuition and time period
     * @param tuitionId the tuition we want to retrieve lessons for
     * @param start period start
     * @param end period end
     * @return a distinct set of lesson DTOS
     */
    public Set<LessonDto> getLessonsByTuitionId(Long tuitionId, LocalDateTime start, LocalDateTime end) {
        tuitionRepository.existsById(tuitionId);
        return lessonRepository.findLessonsByPeriod(tuitionId,start,end)
                .stream()
                .map(lessonMapper::toDto)
                .collect(Collectors.toSet());
    }

    /**
     * delete a lesson
     * @param lessonId the id of the lesson to delete
     */
    @Transactional
    public void cancelLesson(Long lessonId, Boolean resetAvailability) {
        Lesson lesson = findLessonById(lessonId);
        Availability availability = lesson.getAvailability();

        if (resetAvailability) {
            availability.setStatus(AvailabilityStatus.AVAILABLE);
            availabilityService.save(availability);
        } else {
            availabilityService.deleteAvailability(availability);
        }

        lessonRepository.deleteById(lessonId);
    }



    /**
     * Get the set of lessons that exist for a given profile during a given period
     * @param profileId if of the profile to retrieve lessons for
     * @param start period start
     * @param end period end
     * @return set of distinct lessons as dtos
     */
    public Set<LessonDto> getLessonsByProfileId(Long profileId, LocalDateTime start, LocalDateTime end) {

        Set<Lesson> lessons = new HashSet<>();
        Profile profile = profileService.fetchProfileEntityInternal(profileId);
        if(profile.getProfileType().equals(ProfileType.TUTOR)) {
            lessons = lessonRepository.findAllLessonsByTutorId(profileId, start, end);
        } else if (profile.getProfileType().equals(ProfileType.STUDENT)) {
            lessons = lessonRepository.findAllLessonsByStudentId(profileId,start,end);
        }
        return lessons.stream().map(lessonMapper:: toDto).collect(Collectors.toSet());
    }


    /**
     * Get the lesson summamary by the associated availaiblity slot
     * @param availabilityId id of the availability corresponding to the lesson
     * @return lessonSummarydto
     */
    public LessonSummaryDto getLessonSummaryByAvailabilityId(Long availabilityId) {

        return lessonRepository.findLessonSummaryByAvailabilityId(availabilityId);
    }

    /**
     * Fetch a lesson from the db by its id
     * @param lessonId id of the lesson to retrieve
     * @return the lesson corresponding to the id or throw validation exception
     */
    public Lesson findLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ValidationException("Lesson not found with id " + lessonId));
    }

    /**
     * Get all lessons for a given tuition
     * @param studentId the tuition for which to fetch lessons
     * @return set of lesson dtos`
     */
    public Set<LessonDto> getCompletedLessonsByTuitionId(Long studentId, Long tutorId) {
        Long tuitionId = tuitionService.getByProfileIds(studentId,tutorId).getId();
        Set<Lesson> allLessons = lessonRepository.findAllByTuitionId((tuitionId));
        return allLessons.stream()
                .map(lessonMapper::toDto)
                .collect(Collectors.toSet());
    }
}
