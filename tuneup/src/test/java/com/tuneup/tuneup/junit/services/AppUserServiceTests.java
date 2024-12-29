package com.tuneup.tuneup.junit.services;



import com.tuneup.tuneup.users.model.AppUser;
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

import com.tuneup.tuneup.address.AddressDto;
import com.tuneup.tuneup.address.AddressService;
import com.tuneup.tuneup.users.model.PasswordResetToken;
import com.tuneup.tuneup.users.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.UUID;

class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserMapper appUserMapper;

    @Mock
    private AppUserValidator appUserValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AddressService addressService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private AppUserService appUserService;

    private AppUser appUser;
    private AppUserDto appUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        appUser = new AppUser();
        appUser.setId(1L);
        appUser.setUsername("testuser");
        appUser.setEmail("test@example.com");
        appUser.setPassword("password123");

        appUserDto = new AppUserDto();
        appUserDto.setId(1L);
        appUserDto.setUsername("testuser");
        appUserDto.setEmail("test@example.com");
        appUserDto.setPassword("password123");
    }

    @Test
    void createUser_ShouldReturnCreatedUserDto() {
        when(appUserMapper.toAppUser(appUserDto)).thenReturn(appUser);
        when(passwordEncoder.encode(appUser.getPassword())).thenReturn("encodedPassword");
        when(appUserRepository.save(appUser)).thenReturn(appUser);
        when(appUserMapper.toAppUserDto(appUser)).thenReturn(appUserDto);

        AppUserDto result = appUserService.createUser(appUserDto);

        assertNotNull(result);
        assertEquals(appUserDto.getId(), result.getId());
        verify(appUserValidator, times(1)).validateAppUserCreation(appUserDto);
        verify(appUserRepository, times(1)).save(appUser);
    }

    @Test
    void findAll_ShouldReturnListOfUserDtos() {
        List<AppUser> appUsers = Arrays.asList(appUser, appUser);
        when(appUserRepository.findAll()).thenReturn(appUsers);
        when(appUserMapper.toAppUserDto(any(AppUser.class))).thenReturn(appUserDto);

        List<AppUserDto> result = appUserService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appUserRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));

        AppUser result = appUserService.findById(1L);

        assertNotNull(result);
        assertEquals(appUser.getId(), result.getId());
        verify(appUserRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> appUserService.findById(1L));
        assertEquals("AppUser with ID 1 not found", exception.getMessage());
        verify(appUserRepository, times(1)).findById(1L);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setId(1L);

        appUserDto.setAddress(addressDto);

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));
        when(addressService.createAddress(addressDto)).thenReturn(addressDto);
        when(appUserMapper.toAppUser(appUserDto)).thenReturn(appUser);
        when(appUserRepository.save(appUser)).thenReturn(appUser);
        when(appUserMapper.toAppUserDto(appUser)).thenReturn(appUserDto);

        AppUserDto result = appUserService.updateUser(appUserDto);

        assertNotNull(result);
        assertEquals(appUserDto.getId(), result.getId());
        verify(appUserRepository, times(1)).save(appUser);
    }

    @Test
    void verifyPasswordReset_ShouldUpdatePasswordAndDeleteToken() {
        PasswordResetToken resetToken = new PasswordResetToken(appUser, UUID.randomUUID().toString(), LocalDateTime.now().plusMinutes(30));

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        appUserService.verifyPasswordReset(resetToken.getToken(), "newPassword");

        assertEquals("encodedNewPassword", appUser.getPassword());
        verify(appUserRepository, times(1)).save(appUser);
        verify(passwordResetTokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void verifyPasswordReset_ShouldThrowException_WhenTokenExpired() {
        PasswordResetToken resetToken = new PasswordResetToken(appUser, UUID.randomUUID().toString(), LocalDateTime.now().minusMinutes(1));

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> appUserService.verifyPasswordReset(resetToken.getToken(), "newPassword"));

        assertEquals("Token has expired.", exception.getMessage());
        verify(passwordResetTokenRepository, never()).delete(resetToken);
    }

    @Test
    void generateResetToken_ShouldReturnToken() {
        when(appUserRepository.findByEmail("test@example.com")).thenReturn(appUser);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = appUserService.generateResetToken("test@example.com");

        assertNotNull(token);
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
    }
}
