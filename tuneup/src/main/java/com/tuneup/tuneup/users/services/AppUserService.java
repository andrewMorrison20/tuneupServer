package com.tuneup.tuneup.users.services;

import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.repository.AppUser;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    public AppUserDto createUser(AppUserDto appUserDto) {
        //validation here
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
