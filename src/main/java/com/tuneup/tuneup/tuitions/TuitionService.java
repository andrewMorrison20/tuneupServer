package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.profiles.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class TuitionService {

    private final TuitionRepository tuitionRepository;
    private final ProfileService profileService;

    public TuitionService(TuitionRepository tuitionRepository, ProfileService profileService) {
        this.tuitionRepository = tuitionRepository;
        this.profileService = profileService;
    }

    public Tuition createTuition(TuitionDto tuitionDto){

        return null;
    }
}
