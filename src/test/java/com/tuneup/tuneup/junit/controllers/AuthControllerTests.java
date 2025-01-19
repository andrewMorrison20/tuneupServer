package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.authentication.controller.AuthController;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.dtos.LoginRequestDto;
import com.tuneup.tuneup.users.dtos.LoginResponseDto;
import com.tuneup.tuneup.users.services.AppUserService;
import com.tuneup.tuneup.utils.Jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AppUserService appUserService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_ShouldReturnJwtToken() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password");

        AppUserDto userDto = new AppUserDto();
        userDto.setId(1L);
        userDto.setEmail("user@example.com");
        userDto.setName("User");
        userDto.setUsername("Test");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(appUserService.getUserByEmail("user@example.com")).thenReturn(userDto);
        when(jwtUtil.generateToken("user@example.com", 1L)).thenReturn("mock-jwt-token");

        LoginResponseDto response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals(userDto, response.getUserDto());
    }

    @Test
    void logout_ShouldBlacklistToken() {
        String token = "mock-token";
        doNothing().when(jwtUtil).blacklistToken(token);

        ResponseEntity<String> response = authController.logout("Bearer " + token);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Logged out successfully.", response.getBody());
    }
}
