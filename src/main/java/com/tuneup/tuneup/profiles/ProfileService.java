package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.InstrumentMapper;
import com.tuneup.tuneup.Instruments.InstrumentService;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.genres.GenreMapper;
import com.tuneup.tuneup.images.Image;
import com.tuneup.tuneup.images.ImageRepository;
import com.tuneup.tuneup.images.ImageService;
import com.tuneup.tuneup.pricing.Price;
import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.pricing.PriceMapper;
import com.tuneup.tuneup.pricing.PriceValidator;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteriaDto;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;

import com.tuneup.tuneup.profiles.repositories.ProfileSpecification;
import com.tuneup.tuneup.qualifications.ProfileInstrumentQualification;
import com.tuneup.tuneup.qualifications.dtos.ProfileInstrumentQualificationDto;
import com.tuneup.tuneup.qualifications.mappers.ProfileInstrumentQualificationMapper;
import com.tuneup.tuneup.qualifications.mappers.QualificationMapper;
import com.tuneup.tuneup.qualifications.services.QualificationService;
import com.tuneup.tuneup.regions.RegionDto;
import com.tuneup.tuneup.regions.RegionMapper;
import com.tuneup.tuneup.regions.RegionRepository;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.services.AppUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PriceValidator priceValidator;
    private final ImageRepository imageRepository;
    private final RegionRepository regionRepository;
    private final InstrumentService instrumentService;
    private final QualificationMapper qualificationMapper;
    private final QualificationService qualificationService;
    private final AvailabilityRepository availabilityRepository;
    private final ProfileInstrumentQualificationMapper profileInstrumentQualificationMapper;

    public ProfileService(ProfileRepository profileRepository, ProfileMapper profileMapper, ProfileValidator profileValidator,
                          AppUserService appUserService, PriceMapper priceMapper, GenreMapper genreMapper, RegionMapper regionMapper,
                          InstrumentMapper instrumentMapper, ImageService imageService, PriceValidator priceValidator, ImageRepository imageRepository,
                          RegionRepository regionRepository, InstrumentService instrumentService, QualificationMapper qualificationMapper
                          , QualificationService qualificationService, AvailabilityRepository availabilityRepository,ProfileInstrumentQualificationMapper profileInstrumentQualificationMapper) {

        this.appUserService = appUserService;
        this.profileMapper = profileMapper;
        this.profileRepository = profileRepository;
        this.profileValidator = profileValidator;
        this.priceMapper = priceMapper;
        this.genreMapper = genreMapper;
        this.regionMapper = regionMapper;
        this.instrumentMapper = instrumentMapper;
        this.imageService = imageService;
        this.priceValidator = priceValidator;
        this.imageRepository = imageRepository;
        this.regionRepository = regionRepository;
        this.instrumentService = instrumentService;
        this.qualificationMapper = qualificationMapper;
        this.qualificationService = qualificationService;
        this.availabilityRepository = availabilityRepository;
        this.profileInstrumentQualificationMapper = profileInstrumentQualificationMapper;
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
            Image profileImage = imageRepository.findById(profileDto.getProfilePicture().getId()).orElseThrow();
            existingProfile.setProfilePicture(profileImage);
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

        if(profileDto.getDisplayName()!=null){
            existingProfile.setDisplayName(profileDto.getDisplayName());
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

    public Page<ProfileDto> searchProfiles(ProfileSearchCriteriaDto criteria, Pageable page) {
        return profileRepository.findAll(ProfileSpecification.bySearchCriteria(criteria, regionRepository, availabilityRepository),page)
                .map(profileMapper::toProfileDto);
    }

    @Transactional
    public Integer updatePricing(Set<PriceDto> priceDtoSet, Long profileId) {
        Set<Price> profilePricing = priceValidator.validateOrCreatePricing(priceDtoSet);
        Profile profile = profileValidator.fetchById(profileId);
        profile.setPrices(profilePricing);
        profileRepository.save(profile);
        return profilePricing.size();
    }

    @Transactional
    public Integer updateProfileInstrumentQualifications(Long profileId, Set<ProfileInstrumentQualificationDto> qualificationsDto) {
        Profile profile = profileValidator.fetchById(profileId);

        Set<ProfileInstrumentQualification> qualifications = qualificationsDto.stream()
                .map(dto -> createProfileInstrumentQualification(profile, dto))
                .collect(Collectors.toSet());

        profile.setProfileInstrumentQualifications(qualifications);
        profileRepository.save(profile);
        return profile.getProfileInstrumentQualifications().size();
    }

    private ProfileInstrumentQualification createProfileInstrumentQualification(Profile profile, ProfileInstrumentQualificationDto dto) {
        ProfileInstrumentQualification qualification = new ProfileInstrumentQualification();
        qualification.setProfile(profile);
        qualification.setInstrument(instrumentService.getInstrumentByIdInternal(dto.getInstrumentId()));
        qualification.setQualification(qualificationService.getQualificationByIdInternal(dto.getQualificationId()));
        return qualification;
    }

    public Boolean existById(long profileId){
        return profileValidator.existsById(profileId);
    }

    /**
     *For internal use only. For fetching profileDtos for use ion controller layer and repsonses use getProfileDto
     * @return profile
     */
    public Profile fetchProfileEntityInternal(Long id){
       return profileValidator.fetchById(id);
    }

    public Set<ProfileInstrumentQualificationDto> getProfileQualificationsById(Long profileId){
        Profile profile = fetchProfileEntityInternal(profileId);
        return profile.getProfileInstrumentQualifications()
                .stream()
                .map(profileInstrumentQualificationMapper::toDto)
                .collect(Collectors.toSet());
    }
}





