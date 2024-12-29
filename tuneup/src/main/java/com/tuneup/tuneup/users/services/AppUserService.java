package com.tuneup.tuneup.users.services;

import com.tuneup.tuneup.address.AddressDto;
import com.tuneup.tuneup.address.AddressService;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.model.PasswordResetToken;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.repository.PasswordResetTokenRepository;
import com.tuneup.tuneup.users.validators.AppUserValidator;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AppUserValidator appUserValidator;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper, AppUserValidator appUserValidator, PasswordEncoder passwordEncoder, AddressService addressService, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.appUserValidator = appUserValidator;
        this.passwordEncoder = passwordEncoder;
        this.addressService = addressService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Transactional
    public AppUserDto createUser(AppUserDto appUserDto) {
        appUserValidator.validateAppUserCreation(appUserDto);
        AppUser appUser = appUserMapper.toAppUser(appUserDto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUserRepository.save(appUser);
        return appUserMapper.toAppUserDto(appUser);
    }

    public List<AppUserDto> findAll() {
        List<AppUser> appUsers = appUserRepository.findAll();
        return appUsers.stream()
                .map(appUserMapper::toAppUserDto)
                .toList();
    }

    public AppUser findById(Long appUserId) {
        return appUserRepository.findById(appUserId)
                .orElseThrow(() -> new RuntimeException("AppUser with ID " + appUserId + " not found"));
    }

    public AppUserDto getUserByEmail(String email) {
        AppUser user = appUserRepository.findByEmail(email);
        return appUserMapper.toAppUserDto(user);
    }

    /*this should probably use reflection rather than series of conditionals, Also there is a better way to carry out validation,
    should ideally have a single call to the validator, possibly pass an array of changed values along side the new dto and  alter the checks in validator
    one to revisit
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
           AddressDto userAddress = addressService.createAddress(appUserDto.getAddress());
           existingUserDto.setAddress(userAddress);
        }
        AppUser appUser = appUserMapper.toAppUser(existingUserDto);
        AppUserDto updateUserDto= appUserMapper.toAppUserDto(appUserRepository.save(appUser));
        return updateUserDto;
    }

    /*
    *does this need to be transactional? we would still want the token to delete even in the event of invalid user passed
    * and failed update. Immediate deletion of token would prevent retry logic without generating a new token.
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
}
