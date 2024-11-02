package com.tuneup.tuneup.users.services;

import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.repository.AppUser;
import com.tuneup.tuneup.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserService {
    private final UserRepository userRepository;
    private final AppUserMapper appUserMapper;

    public AppUserService(UserRepository userRepository, AppUserMapper appUserMapper) {
        this.userRepository = userRepository;
        this.appUserMapper = appUserMapper;
    }

    public AppUserDto createUser(AppUserDto appUserDto) {
        //validation here
        AppUser appUser = appUserMapper.toAppUser(appUserDto);
        userRepository.save(appUser);
        return  appUserMapper.toAppUserDto(appUser);
    }

    public List<AppUserDto> findAll() {
        List<AppUser> appUsers = userRepository.findAll();
        return appUsers.stream()
                .map(appUserMapper::toAppUserDto)
                .toList();
    }
}
