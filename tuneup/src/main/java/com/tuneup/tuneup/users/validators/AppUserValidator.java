package com.tuneup.tuneup.users.validators;

import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import org.springframework.stereotype.Component;

@Component
public class AppUserValidator {

    private final AppUserRepository appUserRepository;

    public AppUserValidator(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public void validateAppUser(AppUserDto appUserDto){
        checkAppUserId(appUserDto.getId());
    }

    private void checkAppUserId(Long appUserId) {
       if(appUserRepository.existsById(appUserId)){
           throw new ValidationException("App user already exists");
       }
    }
}
