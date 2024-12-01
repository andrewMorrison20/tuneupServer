package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;

import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.services.AppUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final ProfileValidator profileValidator;
    private final AppUserService appUserService;

    public ProfileService(ProfileRepository profileRepository, ProfileMapper profileMapper, ProfileValidator profileValidator, AppUserService appUserService) {
        this.appUserService = appUserService;
        this.profileMapper = profileMapper;
        this.profileRepository = profileRepository;
        this.profileValidator = profileValidator;
    }

    /**
     * Get Page of profiles from db
     * @param pageable
     * @return
     */
    public Page<ProfileDto> findProfilesDto(Pageable pageable) {

           Page<Profile> profiles = profileRepository.findAll(pageable);
           return profiles.map(profileMapper::toProfileDto);
    }

    /**
     * Creates a new profile based on the info passed in the dto.
     * @param profileDto details of profile to create
     * @return dto of successful profile creation. Else throw validation/server exception
     */
    @Transactional
    public ProfileDto createProfile(ProfileDto profileDto) {
        profileValidator.validatorProfileDto(profileDto);
        AppUser appUser = appUserService.findById(profileDto.getAppUserId());
        Profile profile = profileMapper.toProfile(profileDto);
        profile.setAppUser(appUser);
        Profile savedProfile = profileRepository.save(profile);
        return profileMapper.toProfileDto(savedProfile);
    }


    public ProfileDto getProfileDto(Long id) {
            return profileRepository.findById(id)
                    .map(profileMapper::toProfileDto)
                    .orElse(null);
        }

    }





