package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.users.AppUser;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.services.AppUserService;
import com.tuneup.tuneup.users.validators.AppUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserMapper appUserMapper;

    @Mock
    private AppUserValidator appUserValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldSaveUserAndReturnDto() {

        AppUserDto inputDto = new AppUserDto();
        inputDto.setPassword("plainPassword");

        AppUser mockAppUser = new AppUser();
        mockAppUser.setPassword("plainPassword");

        AppUser savedAppUser = new AppUser();
        savedAppUser.setPassword("encodedPassword");

        AppUserDto expectedDto = new AppUserDto();

        when(appUserMapper.toAppUser(any())).thenReturn(mockAppUser);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedAppUser);
        when(appUserMapper.toAppUserDto(any(AppUser.class))).thenReturn(expectedDto);

        AppUserDto result = appUserService.createUser(inputDto);

        verify(appUserValidator).validateAppUser(inputDto);
        verify(appUserMapper).toAppUser(inputDto);
        verify(passwordEncoder).encode("plainPassword");
        verify(appUserRepository).save(mockAppUser);


        assertEquals(expectedDto, result);
    }

    @Test
    void findAll_shouldReturnListOfAppUserDtos() {

        AppUser mockApp1User = new AppUser();
        AppUser mockApp2User = new AppUser();
        AppUser mockApp3User = new AppUser();

        AppUserDto dto1 = new AppUserDto();
        AppUserDto dto2 = new AppUserDto();
        AppUserDto dto3 = new AppUserDto();

        when(appUserRepository.findAll()).thenReturn(List.of(mockApp1User, mockApp2User, mockApp3User));
        when(appUserMapper.toAppUserDto(mockApp1User)).thenReturn(dto1);
        when(appUserMapper.toAppUserDto(mockApp2User)).thenReturn(dto2);
        when(appUserMapper.toAppUserDto(mockApp3User)).thenReturn(dto3);


        List<AppUserDto> result = appUserService.findAll();

        verify(appUserRepository).findAll();
        verify(appUserMapper).toAppUserDto(mockApp1User);
        verify(appUserMapper).toAppUserDto(mockApp2User);
        verify(appUserMapper).toAppUserDto(mockApp3User);

        assertEquals(3, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        assertTrue(result.contains(dto3));
    }

    @Test
    void findById_shouldReturnAppUserIfFound() {
        Long appUserId = 1L;
        AppUser mockAppUser = new AppUser();

        when(appUserRepository.findById(appUserId)).thenReturn(Optional.of(mockAppUser));

        AppUser result = appUserService.findById(appUserId);

        verify(appUserRepository).findById(appUserId);
        assertEquals(mockAppUser, result);
    }

    @Test
    void findById_shouldThrowExceptionIfNotFound() {

        Long appUserId = 1L;

        when(appUserRepository.findById(appUserId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> appUserService.findById(appUserId));
        assertEquals("AppUser with ID " + appUserId + " not found", exception.getMessage());

        verify(appUserRepository).findById(appUserId);
    }
}