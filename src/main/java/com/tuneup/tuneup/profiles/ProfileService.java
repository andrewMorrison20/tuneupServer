package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.InstrumentMapper;
import com.tuneup.tuneup.genres.GenreMapper;
import com.tuneup.tuneup.images.ImageService;
import com.tuneup.tuneup.pricing.Price;
import com.tuneup.tuneup.pricing.PriceMapper;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteria;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;

import com.tuneup.tuneup.profiles.repositories.ProfileSpecification;
import com.tuneup.tuneup.regions.RegionDto;
import com.tuneup.tuneup.regions.RegionMapper;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.services.AppUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final ProfileValidator profileValidator;
    private final AppUserService appUserService;
    private final PriceMapper priceMapper;
    private final GenreMapper genreMapper;
    private final RegionMapper regionMapper;
    private final InstrumentMapper instrumentMapper;
    private final ImageService imageService;

    public ProfileService(ProfileRepository profileRepository, ProfileMapper profileMapper, ProfileValidator profileValidator, AppUserService appUserService, PriceMapper priceMapper, GenreMapper genreMapper, RegionMapper regionMapper, InstrumentMapper instrumentMapper, ImageService imageService) {
        this.appUserService = appUserService;
        this.profileMapper = profileMapper;
        this.profileRepository = profileRepository;
        this.profileValidator = profileValidator;
        this.priceMapper = priceMapper;
        this.genreMapper = genreMapper;
        this.regionMapper = regionMapper;
        this.instrumentMapper = instrumentMapper;
        this.imageService = imageService;
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


    public ProfileDto getProfileDtoByUserId(Long userId) {
        profileValidator.existsByUser(userId);
        Profile profile =  profileRepository.findByAppUserId(userId);
        return profileMapper.toProfileDto(profile);
    }

    public ProfileDto getProfileDto(Long id) {
            return profileRepository.findById(id)
                    .map(profileMapper::toProfileDto)
                    .orElse(null);
        }


    @Transactional
    public ProfileDto updateProfile(ProfileDto profileDto) {
        Profile existingProfile = profileRepository.findById(profileDto.getId())
                .orElseThrow();
        //TO-DO extend this either using beansUtils or Mapper and custom logic to cover all fields of profile
        //Need to think about fetching from db as oppossed to front end always sending names with sub dtos.

        if(profileDto.getProfilePicture()!=null){
            //imageService.uploadFile()
        }
        if(profileDto.getInstruments() != null) {
            existingProfile.setInstruments(profileDto.getInstruments()
                    .stream()
                    .map(instrumentMapper::toInstrument)
                    .collect(Collectors.toSet()));
        }

        if(profileDto.getPrices() !=null) {
            Set<Price> updatedPrices = profileDto.getPrices()
                    .stream()
                    .map(priceMapper::toPrice)
                    .collect(Collectors.toSet());
            existingProfile.setPrices(updatedPrices);
        }

        if(profileDto.getGenres()!=null){
            existingProfile.setGenres(
                    profileDto.getGenres().stream()
                            .map(genreMapper:: toGenre)
                            .collect(Collectors.toSet())
            );
        }

        if(profileDto.getProfileType()!=null){
            existingProfile.setProfileType(profileDto.getProfileType());
        }

        if(profileDto.getBio()!=null){
            existingProfile.setBio(profileDto.getBio());
        }

        if(profileDto.getTuitionRegion()!=null){
            RegionDto regionDto = profileDto.getTuitionRegion();

            existingProfile.setTuitionRegion(regionMapper.toRegion(profileDto.getTuitionRegion()));
        }

        Profile profile = profileRepository.save(existingProfile);

        return profileMapper.toProfileDto(profile);
    }

    public Page<ProfileDto> searchProfiles(ProfileSearchCriteria criteria, Pageable page) {
        return profileRepository.findAll(ProfileSpecification.bySearchCriteria(criteria),page)
                .map(profileMapper::toProfileDto);
    }
}





