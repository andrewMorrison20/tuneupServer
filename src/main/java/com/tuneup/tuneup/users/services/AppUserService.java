package com.tuneup.tuneup.users.services;

import com.tuneup.tuneup.address.dtos.AddressDto;
import com.tuneup.tuneup.address.services.AddressService;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.model.EmailVerificationToken;
import com.tuneup.tuneup.users.model.PasswordResetToken;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.repository.EmailVerificationTokenRepository;
import com.tuneup.tuneup.users.repository.PasswordResetTokenRepository;
import com.tuneup.tuneup.users.validators.AppUserValidator;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AppUserValidator appUserValidator;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ProfileRepository profileRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;

    public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper, AppUserValidator appUserValidator, PasswordEncoder passwordEncoder, AddressService addressService, PasswordResetTokenRepository passwordResetTokenRepository, ProfileRepository profileRepository, EmailVerificationTokenRepository emailVerificationTokenRepository, EmailService emailService) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.appUserValidator = appUserValidator;
        this.passwordEncoder = passwordEncoder;
        this.addressService = addressService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.profileRepository = profileRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
    }

    /**
     * Create a new user
     * @param appUserDto user to create
     * @param profileType type of profile to create for user
     * @return AppUserDto newly created user
     */
    @Transactional
    public AppUserDto createUser(AppUserDto appUserDto,ProfileType profileType) {
        appUserValidator.validateAppUserCreation(appUserDto);
        AppUser appUser = appUserMapper.toAppUser(appUserDto);
        appUser.setVerified(false);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser = appUserRepository.save(appUser);
        sendVerificationEmail(appUser.getEmail());
        Profile defaultProfile = new Profile();
        defaultProfile.setAppUser(appUser);
        defaultProfile.setDisplayName(appUser.getName());
        defaultProfile.setProfileType(profileType);
        profileRepository.save(defaultProfile);
        return appUserMapper.toAppUserDto(appUser);
    }

    /**
     * Generates a verification link and sends it to a users email if registered
     * @param email the registered email address
     */

    @Transactional
    public void sendVerificationEmail(String email) {
       AppUser user = appUserRepository.findByEmail(email);

        Optional<EmailVerificationToken> existingToken = emailVerificationTokenRepository.findByUserId(user.getId());
        existingToken.ifPresent(emailVerificationTokenRepository::delete);
        emailVerificationTokenRepository.flush();
        String token = generateVerificationToken(user);
        String verificationUrl = "http://localhost:4200/login/verified?token=" + token;

        emailService.sendVerificationEmail(email, verificationUrl);
    }

    /**
     * Retrieve all users
     * @return List AppUSerDto the list of existing users
     */
    public List<AppUserDto> findAll() {
        List<AppUser> appUsers = appUserRepository.findAll();
        return appUsers.stream()
                .map(appUserMapper::toAppUserDto)
                .toList();
    }

    /**
     * Find a user by their associatedId
     * @param appUserId id of user to find
     * @return AppUser the user entity
     */
    public AppUser findById(Long appUserId) {
        return appUserRepository.findById(appUserId)
                .orElseThrow(() -> new RuntimeException("AppUser with ID " + appUserId + " not found"));
    }

    /**
     * Fetch a user account entity by their associated email address
     * @param email the email of the app account to retrieve
     * @return AppUserDto - user account
     */
    public AppUserDto getUserByEmail(String email) {
        AppUser user = appUserRepository.findByEmail(email);
        return appUserMapper.toAppUserDto(user);
    }

    /*this should probably use reflection rather than series of conditionals, Also there is a better way to carry out validation,
    should ideally have a single call to the validator, possibly pass an array of changed values along side the new dto and  alter the checks in validator
    one to revisit
     */

    /**
     * Update a user account
     * @param appUserDto updated fields
     * @return AppUserDto updated account as Dto
     */
    @Transactional
    public AppUserDto updateUser(AppUserDto appUserDto) {
        AppUser existingUser = findById(appUserDto.getId());
        AppUserDto existingUserDto = appUserMapper.toAppUserDto(existingUser);

        if(appUserDto.getPassword()!=null) {
            appUserValidator.checkPassword(appUserDto.getPassword());
            existingUserDto.setPassword(passwordEncoder.encode(appUserDto.getPassword()));
        }

        if(appUserDto.getUsername()!=null) {
            appUserValidator.checkUsername(appUserDto.getUsername());
            existingUserDto.setUsername(appUserDto.getUsername());
        }
        if(appUserDto.getName()!=null) {

            existingUserDto.setName(appUserDto.getName());
        }

        if(appUserDto.getEmail()!=null) {
            appUserValidator.checkEmail(appUserDto.getEmail());
            existingUserDto.setEmail(appUserDto.getEmail());
        }

        if(appUserDto.getAddress()!=null){
           AddressDto userAddress = addressService.createOrUpdateAddress(appUserDto.getAddress());
           existingUserDto.setAddress(userAddress);
        }
        AppUser appUser = appUserMapper.toAppUser(existingUserDto);
        AppUserDto updateUserDto= appUserMapper.toAppUserDto(appUserRepository.save(appUser));
        return updateUserDto;
    }

    /**
     * Verify a password reset request based on JWT
     * @param token temporary access token
     * @param newPassword updated password
     */
    @Transactional
    public void verifyPasswordReset(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired.");
        }

        AppUser user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    /**
     * Carry out email verification for a registered account
     * @param token Temporary jwt to auth on
     */
    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid or expired token."));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Token has expired.");
        }

        AppUser user = verificationToken.getUser();
        user.setVerified(true);
        appUserRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);
    }

    /**
     * Create a password reset token for user
     * @param email the email address to send the token to
     * @return the temporary token
     */
    public String generateResetToken(String email) {
       AppUser user = appUserRepository.findByEmail(email);
       if(user == null){
           throw new ValidationException("User with this email address, does not exist");
       }
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                user,
                token,
                LocalDateTime.now().plusHours(1)
        );
        passwordResetTokenRepository.save(resetToken);
        return token;
    }

    /**
     * Generate a temporary verification token
     * @param user user to generate token for
     * @return temporary token
     */
    public String generateVerificationToken(AppUser user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                user,
                token,
                LocalDateTime.now().plusHours(24)
        );
        emailVerificationTokenRepository.save(verificationToken);
        return token;
    }

    /**
     * Marks users and their associated profiles as deleted. Soft delete only. Retains all records.
     * Primarily for admin console
     * @param userIds ids of the users to soft-delete
     */
    @Transactional
    public void softDeleteUsers(List<Long> userIds) {
        LocalDateTime time = LocalDateTime.now();
        appUserRepository.softDeleteUsersByIds(userIds, time);
        profileRepository.softDeleteProfilesByUserIds(userIds, time);
    }

    //note this is a single method as refactoring the profile logic would require setting self in bean context to ensure transactional
    //Other option is to refactor to profile service but then beans would have to be lazy loaded.

    /**
     * Anonymise a user and remove all their personal details
     * @param userId id of the user to anonymise
     */
    @Transactional
    public void anonymiseUserById(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        String uid = UUID.randomUUID().toString().substring(0, 8);
        user.setName("Deleted User");
        user.setEmail("deleted_" + uid + "@tuneup.local");
        user.setUsername("deleted_" + uid);
        user.setPassword(null);
        user.setAddress(null);
        user.setVerified(false);
        user.setDeletedAt(LocalDateTime.now());

        Profile profile = profileRepository.findByAppUserId(user.getId());
        if (profile != null) {
            profile.setDisplayName("Deleted User");
            profile.setBio(null);
            profile.setProfilePicture(null);
            profile.setInstruments(null);
            profile.setPrices(null);
            profile.setGenres(null);
            profile.setTuitionRegion(null);
            profile.setProfileInstrumentQualifications(null);
            profile.setDeletedAt(LocalDateTime.now());
            profileRepository.save(profile);
        }

        appUserRepository.save(user);
    }
}
