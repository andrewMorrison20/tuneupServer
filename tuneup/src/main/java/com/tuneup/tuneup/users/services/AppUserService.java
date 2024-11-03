package com.tuneup.tuneup.users.services;

import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.AppUser;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.validators.AppUserValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AppUserValidator appUserValidator;

    public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper, AppUserValidator appUserValidator) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.appUserValidator = appUserValidator;
    }

    @Transactional
    public AppUserDto createUser(AppUserDto appUserDto) {
        appUserValidator.validateAppUser(appUserDto);
        AppUser appUser = appUserMapper.toAppUser(appUserDto);
        appUserRepository.save(appUser);
        return  appUserMapper.toAppUserDto(appUser);
    }

    public List<AppUserDto> findAll() {
        List<AppUser> appUsers = appUserRepository.findAll();
        return appUsers.stream()
                .map(appUserMapper::toAppUserDto)
                .toList();
    }
}
