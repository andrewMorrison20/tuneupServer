package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.mappers.InstrumentMapper;
import com.tuneup.tuneup.Instruments.services.InstrumentService;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.genres.mappers.GenreMapper;
import com.tuneup.tuneup.images.Image;
import com.tuneup.tuneup.images.ImageRepository;
import com.tuneup.tuneup.pricing.entities.Price;
import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.pricing.mappers.PriceMapper;
import com.tuneup.tuneup.pricing.services.PriceValidator;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteriaDto;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.mappers.ProfileMapper;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;

import com.tuneup.tuneup.profiles.repositories.ProfileSpecification;
import com.tuneup.tuneup.qualifications.ProfileInstrumentQualification;
import com.tuneup.tuneup.qualifications.dtos.ProfileInstrumentQualificationDto;
import com.tuneup.tuneup.qualifications.mappers.ProfileInstrumentQualificationMapper;
import com.tuneup.tuneup.qualifications.repositories.ProfileInstrumentQualificationRepository;
import com.tuneup.tuneup.qualifications.services.QualificationService;
import com.tuneup.tuneup.regions.RegionDto;
import com.tuneup.tuneup.regions.mappers.RegionMapper;
import com.tuneup.tuneup.regions.repositories.RegionRepository;
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
    private final PriceValidator priceValidator;
    private final ImageRepository imageRepository;
    private final RegionRepository regionRepository;
    private final InstrumentService instrumentService;
    private final QualificationService qualificationService;
    private final AvailabilityRepository availabilityRepository;
    private final ProfileInstrumentQualificationMapper profileInstrumentQualificationMapper;
    private final ProfileInstrumentQualificationRepository profileInstrumentQualificationRepository;

    public ProfileService(ProfileRepository profileRepository,
                          ProfileMapper profileMapper,
                          ProfileValidator profileValidator,
                          AppUserService appUserService,
                          PriceMapper priceMapper,
                          GenreMapper genreMapper,
                          RegionMapper regionMapper,
                          InstrumentMapper instrumentMapper,
                          PriceValidator priceValidator,
                          ImageRepository imageRepository,
                          RegionRepository regionRepository,
                          InstrumentService instrumentService,
                          QualificationService qualificationService,
                          AvailabilityRepository availabilityRepository,
                          ProfileInstrumentQualificationMapper profileInstrumentQualificationMapper,
                          ProfileInstrumentQualificationRepository profileInstrumentQualificationRepository) {

        this.appUserService = appUserService;
        this.profileMapper = profileMapper;
        this.profileRepository = profileRepository;
        this.profileValidator = profileValidator;
        this.priceMapper = priceMapper;
        this.genreMapper = genreMapper;
        this.regionMapper = regionMapper;
        this.instrumentMapper = instrumentMapper;
        this.priceValidator = priceValidator;
        this.imageRepository = imageRepository;
        this.regionRepository = regionRepository;
        this.instrumentService = instrumentService;
        this.qualificationService = qualificationService;
        this.availabilityRepository = availabilityRepository;
        this.profileInstrumentQualificationMapper = profileInstrumentQualificationMapper;
        this.profileInstrumentQualificationRepository = profileInstrumentQualificationRepository;
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


    /**
     * Find a profile that relates to a given user by their user id
     * @param userId the  id of the user to find a profile for
     * @return profile as a dto
     */
    public ProfileDto getProfileDtoByUserId(Long userId) {
        profileValidator.existsByUser(userId);
        Profile profile =  profileRepository.findByAppUserId(userId);
        return profileMapper.toProfileDto(profile);
    }

    /**
     * Return the profile relating to a given id as a dto
     * @param id the iod of profile to find
     * @return profile from the db as a dto
     */
    public ProfileDto getProfileDto(Long id) {
            return profileRepository.findById(id)
                    .map(profileMapper::toProfileDto)
                    .orElse(null);
        }

    /**
     * update a given profile
     * @param profileDto updated profile as a dto
     * @return the saved profile as a dto
     */
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

        if(profileDto.getLessonType() != null){
            existingProfile.setLessonType(profileDto.getLessonType());
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


    /**
     * find profiles that match a given criteria
     * @param criteria The specification to search for
     * @param page pag number to return
     * @return a page of profilesa matching the criteria as dtos
     */
    public Page<ProfileDto> searchProfiles(ProfileSearchCriteriaDto criteria, Pageable page) {
        return profileRepository.findAll(ProfileSpecification.bySearchCriteria(criteria, regionRepository, availabilityRepository),page)
                .map(profileMapper::toProfileDto);
    }

    /**
     * Updates the pricing for a given profile
     * @param priceDtoSet the set of pricing (period and rate) to update
     * @param profileId the id of the profile to update pricing for
     * @return rows effected
     */
    @Transactional
    public Integer updatePricing(Set<PriceDto> priceDtoSet, Long profileId) {
        Set<Price> profilePricing = priceValidator.validateOrCreatePricing(priceDtoSet);
        Profile profile = profileValidator.fetchById(profileId);
        profile.setPrices(profilePricing);
        profileRepository.save(profile);
        return profilePricing.size();
    }

    /**
     * Update the set of instrument qualifications for a give profile
     * @param profileId id of the profile to update
     * @param qualificationsDto the combination of instruments and qualifications
     * @return number of rows effected
     */
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

    /**
     * Create a new profile qualification combination for a given profile
     * @param profile the profile to update
     * @param dto the instrument and qualification combination
     * @return the saved qualification instrument as a dto
     */
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
     * For internal use only. For fetching profileDtos for use ion controller layer and repsonses use getProfileDto
     * @return profile enitity for given id
     */
    public Profile fetchProfileEntityInternal(Long id){
       return profileValidator.fetchById(id);
    }

    public Set<ProfileInstrumentQualificationDto> getProfileQualificationsById(Long profileId) {
        List<ProfileInstrumentQualification> quals = profileInstrumentQualificationRepository.findByProfileId(profileId);
        return quals.stream()
                .map(profileInstrumentQualificationMapper::toDto)
                .collect(Collectors.toSet());
    }

    public Page<ProfileDto> getProfilesWithoutChatHistory(Long profileId, boolean isTutor, boolean active, Pageable pageable) {
        return profileRepository.findProfilesWithoutChatHistory(profileId, isTutor, active, pageable)
                .map(profileMapper::toProfileDto);
    }
}
