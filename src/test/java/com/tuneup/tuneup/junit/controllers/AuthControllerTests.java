package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.authentication.controller.AuthController;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.dtos.LoginRequestDto;
import com.tuneup.tuneup.users.dtos.LoginResponseDto;
import com.tuneup.tuneup.users.exceptions.EmailNotVerifiedException;
import com.tuneup.tuneup.users.services.AppUserService;
import com.tuneup.tuneup.utils.Jwt.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

    @Mock AuthenticationManager authenticationManager;
    @Mock AppUserService appUserService;
    @Mock ProfileService profileService;
    @Mock JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_Success() throws Exception {
        // Prepare request
        LoginRequestDto req = new LoginRequestDto();
        req.setEmail("user@example.com");
        req.setPassword("secret");

        // Stub authenticationManager
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("user@example.com","secret")
        )).thenReturn(auth);

        // Stub AppUserDto
        AppUserDto userDto = new AppUserDto();
        userDto.setId(10L);
        userDto.setName("Alice");
        userDto.setVerified(true);
        userDto.setRoles(Collections.emptySet()); // no roles
        when(appUserService.getUserByEmail("user@example.com")).thenReturn(userDto);

        // Stub ProfileDto
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(20L);
        profileDto.setProfileType(ProfileType.TUTOR);
        when(profileService.getProfileDtoByUserId(10L)).thenReturn(profileDto);

        // Stub token generation
        when(jwtUtil.generateToken(
                eq("user@example.com"),
                eq("Alice"),
                eq(10L),
                eq(20L),
                eq(ProfileType.TUTOR),
                eq(Collections.emptyList())
        )).thenReturn("jwt-token-123");

        // Call
        LoginResponseDto resp = authController.login(req);

        // Verify
        assertEquals("jwt-token-123", resp.getToken());
        // Authentication should have been set in SecurityContext
        assertSame(auth, SecurityContextHolder.getContext().getAuthentication());
        verify(appUserService).getUserByEmail("user@example.com");
        verify(profileService).getProfileDtoByUserId(10L);
        verify(jwtUtil).generateToken(
                "user@example.com","Alice",10L,20L,ProfileType.TUTOR,Collections.emptyList()
        );
    }

    @Test
    void login_NotVerified_ThrowsEmailNotVerifiedException() {
        LoginRequestDto req = new LoginRequestDto();
        req.setEmail("user@example.com");
        req.setPassword("secret");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        AppUserDto userDto = new AppUserDto();
        userDto.setId(10L);
        userDto.setVerified(false);
        when(appUserService.getUserByEmail("user@example.com")).thenReturn(userDto);

        EmailNotVerifiedException ex = assertThrows(
                EmailNotVerifiedException.class,
                () -> authController.login(req)
        );
        assertEquals("Email is not verified. Please verify your email before logging in.", ex.getMessage());
    }

    @Test
    void login_AuthManagerFails_PropagatesException() {
        LoginRequestDto req = new LoginRequestDto();
        req.setEmail("user@example.com");
        req.setPassword("badpwd");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad creds"));

        BadCredentialsException ex = assertThrows(
                BadCredentialsException.class,
                () -> authController.login(req)
        );
        assertEquals("Bad creds", ex.getMessage());
    }

    @Test
    void logout_BlacklistsToken_ReturnsOk() {
        // Call with header
        ResponseEntity<String> resp = authController.logout("Bearer abc.def.ghi");

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("Logged out successfully.", resp.getBody());
        verify(jwtUtil).blacklistToken("abc.def.ghi");
    }
}
