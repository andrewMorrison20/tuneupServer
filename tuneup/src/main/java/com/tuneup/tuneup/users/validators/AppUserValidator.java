package com.tuneup.tuneup.users.validators;

import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class AppUserValidator {

    private final AppUserRepository appUserRepository;

    public AppUserValidator(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public void validateAppUser(AppUserDto appUserDto) {

        checkUsername(appUserDto.getUsername());
        checkPassword(appUserDto.getPassword());
        checkEmail(appUserDto.getEmail());
        checkAppUserId(appUserDto.getId());
    }

    private void checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new ValidationException("Username cannot be null or empty");
        }
        if (appUserRepository.existsByUsername(username)) {
            throw new ValidationException("Username already exists");
        }
    }

    private void checkPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }

    }

    private void checkEmail(String email) {
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ValidationException("Invalid email format");
        }
         if(appUserRepository.existsByEmail(email)) {
           throw new ValidationException("Email already exists");
         }
    }

    private void checkAppUserId(Long appUserId) {
        if (appUserId != null && appUserRepository.existsById(appUserId)) {
            throw new ValidationException("App user already exists");
        }
    }
}
