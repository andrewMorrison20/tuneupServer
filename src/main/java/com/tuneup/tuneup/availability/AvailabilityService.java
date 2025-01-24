package com.tuneup.tuneup.availability;
import org.springframework.stereotype.Service;
import com.tuneup.tuneup.profiles.ProfileValidator;
import java.util.List;
import java.util.Set;

@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final ProfileValidator profileValidator;

    public AvailabilityService(AvailabilityRepository availabilityRepository, ProfileValidator profileValidator) {
        this.availabilityRepository = availabilityRepository;
        this.profileValidator = profileValidator;
    }

    public Set<Availability> getUnbookedAvailabilityByProfile(Long profileId) {

        profileValidator.validateProfileId(profileId);
        return availabilityRepository.findByProfileIdAndStatus(profileId, AvailabilityStatus.AVAILABLE);
    }

    public Set<Availability> getAllAvailabilityByProfile(Long profileId) {
        profileValidator.validateProfileId(profileId);
        return availabilityRepository.findByProfileId(profileId);
    }
}
