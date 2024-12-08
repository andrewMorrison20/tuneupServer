package com.tuneup.tuneup.users.services;

import com.tuneup.tuneup.address.Address;
import com.tuneup.tuneup.address.AddressDto;
import com.tuneup.tuneup.address.AddressService;
import com.tuneup.tuneup.users.Operation;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.validators.AppUserValidator;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AppUserValidator appUserValidator;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;

    public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper, AppUserValidator appUserValidator, PasswordEncoder passwordEncoder,AddressService addressService) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.appUserValidator = appUserValidator;
        this.passwordEncoder = passwordEncoder;
        this.addressService = addressService;
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

}
