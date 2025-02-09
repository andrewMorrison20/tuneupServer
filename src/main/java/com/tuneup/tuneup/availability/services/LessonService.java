package com.tuneup.tuneup.availability.services;


import com.tuneup.tuneup.availability.Lesson;
import com.tuneup.tuneup.availability.dtos.LessonDto;
import com.tuneup.tuneup.availability.enums.LessonStatus;
import com.tuneup.tuneup.availability.mappers.LessonMapper;
import com.tuneup.tuneup.availability.repositories.LessonRepository;
import com.tuneup.tuneup.tuitions.TuitionRepository;
import com.tuneup.tuneup.tuitions.TuitionService;
import com.tuneup.tuneup.tuitions.TuitionValidator;
import org.springframework.stereotype.Service;

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

    public LessonService(LessonMapper lessonMapper, LessonRepository lessonRepository, TuitionValidator tuitionValidator, TuitionRepository tuitionRepository, TuitionService tuitionService) {
        this.lessonMapper = lessonMapper;
        this.lessonRepository = lessonRepository;
        this.tuitionValidator = tuitionValidator;
        this.tuitionRepository = tuitionRepository;
        this.tuitionService = tuitionService;
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

    public Set<LessonDto> getLessonsByTuitionId(Long tuitionId) {
        tuitionRepository.existsById(tuitionId);
        return lessonRepository.findAllByTuitionId(tuitionId)
                .stream()
                .map(lessonMapper::toDto)
                .collect(Collectors.toSet());
    }

    public void cancelLesson(Long lessonId) {
    }
}
