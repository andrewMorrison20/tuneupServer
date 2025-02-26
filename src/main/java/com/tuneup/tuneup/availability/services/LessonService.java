package com.tuneup.tuneup.availability.services;


import com.tuneup.tuneup.availability.Lesson;
import com.tuneup.tuneup.availability.dtos.LessonDto;
import com.tuneup.tuneup.availability.dtos.LessonSummaryDto;
import com.tuneup.tuneup.availability.enums.LessonStatus;
import com.tuneup.tuneup.availability.mappers.LessonMapper;
import com.tuneup.tuneup.availability.repositories.LessonRepository;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.tuitions.TuitionRepository;
import com.tuneup.tuneup.tuitions.TuitionService;
import com.tuneup.tuneup.tuitions.TuitionValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private final LessonMapper lessonMapper;
    private final LessonRepository lessonRepository;
    private final TuitionValidator tuitionValidator;
    private final TuitionRepository tuitionRepository;
    private final TuitionService tuitionService;
    private final ProfileService profileService;

    public LessonService(LessonMapper lessonMapper, LessonRepository lessonRepository, TuitionValidator tuitionValidator, TuitionRepository tuitionRepository, TuitionService tuitionService, ProfileService profileService) {
        this.lessonMapper = lessonMapper;
        this.lessonRepository = lessonRepository;
        this.tuitionValidator = tuitionValidator;
        this.tuitionRepository = tuitionRepository;
        this.tuitionService = tuitionService;
        this.profileService = profileService;
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
    public void cancelLesson(Long lessonId) {
       if(lessonRepository.existsById(lessonId)){
           lessonRepository.deleteById(lessonId);
       }
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


    public LessonSummaryDto getLessonSummaryByAvailabilityId(Long availabilityId) {

        return lessonRepository.findLessonSummaryByAvailabilityId(availabilityId);
    }
}
