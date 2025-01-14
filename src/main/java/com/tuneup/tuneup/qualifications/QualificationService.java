package com.tuneup.tuneup.qualifications;

import com.tuneup.tuneup.common.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QualificationService {

    private final QualificationRepository qualificationRepository;
    private final QualificationMapper qualificationMapper;
    private final QualificationValidator qualificationValidator;

    @Autowired
    public QualificationService(QualificationRepository qualificationRepository, QualificationMapper qualificationMapper, QualificationValidator qualificationValidator) {
        this.qualificationRepository = qualificationRepository;
        this.qualificationMapper = qualificationMapper;
        this.qualificationValidator = qualificationValidator;
    }


    public Set<QualificationDto> getAllQualifications() {
        return qualificationRepository.findAll().stream().
        map(qualificationMapper::toQualificationDto)
                .collect(Collectors.toSet());
    }


    public QualificationDto getQualificationById(Long id) {
        // Validate and fetch the qualification
        Qualification qualification = qualificationValidator.validateAndFetchById(id);

        // Convert to DTO and return
        return qualificationMapper.toQualificationDto(qualification);
    }

    public QualificationDto addQualification(QualificationDto qualificationDto) {
        qualificationValidator.validateQualification(OperationType.CREATE,qualificationDto);
        Qualification qualification = qualificationMapper.toQualification(qualificationDto);
        qualification =  qualificationRepository.save(qualification);
        return qualificationMapper.toQualificationDto(qualification);
    }

    @Transactional
    public List<QualificationDto> batchCreateQualifications(List<QualificationDto> qualificationDtos) {
        // Validate and map DTOs to entities
        List<Qualification> qualifications = qualificationDtos.stream()
                .map(dto -> {
                    qualificationValidator.validateQualification(OperationType.CREATE, dto);
                    return qualificationMapper.toQualification(dto);
                })
                .collect(Collectors.toList());

        // Save all entities in batch
        List<Qualification> savedQualifications = qualificationRepository.saveAll(qualifications);

        // Map saved entities to DTOs and return
        return savedQualifications.stream()
                .map(qualificationMapper::toQualificationDto)
                .collect(Collectors.toList());
    }

    public QualificationDto updateQualification(Long id, QualificationDto updatedQualificationDto) {
        // Validate the input for update and fetch the existing qualification
        Qualification existingQualification = qualificationValidator.validateQualification(OperationType.UPDATE, updatedQualificationDto);

        // Update the entity and save
        existingQualification.setName(updatedQualificationDto.getName());
        Qualification updatedQualification = qualificationRepository.save(existingQualification);

        // Convert to DTO and return
        return qualificationMapper.toQualificationDto(updatedQualification);
    }

    // Delete a qualification
    public void deleteQualification(Long id) {
        Qualification qualification = qualificationValidator.validateAndFetchById(id);
        qualificationRepository.delete(qualification);
        qualificationValidator.validateDeletion(id);
        }
    }

