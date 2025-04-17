package com.tuneup.tuneup.junit.controllers;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.users.controller.AppUserController;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.dtos.PasswordResetRequestDto;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.services.AppUserService;
import com.tuneup.tuneup.users.services.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserControllerTests {

    @Mock
    private AppUserService appUserService;
    @Mock
    private AppUserMapper appUserMapper;
    @Mock
    private EmailService emailService;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AppUserController controller;

    private AppUserDto dto1;
    private AppUserDto dto2;

    @BeforeEach
    void setUp() {
        dto1 = new AppUserDto();
        dto1.setId(1L);
        dto1.setEmail("a@example.com");
        dto1.setUsername("alice");

        dto2 = new AppUserDto();
        dto2.setId(2L);
        dto2.setEmail("b@example.com");
        dto2.setUsername("bob");
    }

    @Test
    void getAllUsers_ReturnsList() {
        when(appUserService.findAll()).thenReturn(List.of(dto1, dto2));

        ResponseEntity<List<AppUserDto>> resp = controller.getAllUsers();

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(List.of(dto1, dto2), resp.getBody());
        verify(appUserService).findAll();
    }

    @Test
    void getUserByUsername_ReturnsDto() {
        when(appUserService.getUserByEmail("a@b.com")).thenReturn(dto1);

        ResponseEntity<AppUserDto> resp = controller.getUserByUsername("a@b.com");

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto1, resp.getBody());
        verify(appUserService).getUserByEmail("a@b.com");
    }

    @Test
    void getUserDetails_ById_ReturnsDto() {
        AppUser model = new AppUser();
        when(appUserService.findById(2L)).thenReturn(model);
        when(appUserMapper.toAppUserDto(model)).thenReturn(dto2);

        ResponseEntity<AppUserDto> resp = controller.getUserDetails(2L);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto2, resp.getBody());
        verify(appUserService).findById(2L);
        verify(appUserMapper, times(2)).toAppUserDto(model);
    }

    @Test
    void updateUser_ReturnsUpdated() {
        when(appUserService.updateUser(dto1)).thenReturn(dto2);

        ResponseEntity<AppUserDto> resp = controller.updateUser(dto1);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto2, resp.getBody());
        verify(appUserService).updateUser(dto1);
    }

    @Test
    void createUser_ReturnsCreated() {
        when(appUserService.createUser(dto1, ProfileType.STUDENT)).thenReturn(dto2);

        ResponseEntity<AppUserDto> resp = controller.createUser(dto1, ProfileType.STUDENT);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto2, resp.getBody());
        verify(appUserService).createUser(dto1, ProfileType.STUDENT);
    }

    @Test
    void requestNewVerificationLink_CallsService() {
        ResponseEntity<String> resp = controller.requestNewVerificationLink("a@b.com");
        assertEquals(200, resp.getStatusCodeValue());
        verify(appUserService).sendVerificationEmail("a@b.com");
    }

    @Test
    void resetPassword_SendsEmail() {
        when(appUserService.generateResetToken("a@b.com")).thenReturn("token123");

        Map<String, String> body = new HashMap<>();
        body.put("email", "a@b.com");

        ResponseEntity<Map<String, String>> resp = controller.resetPassword(body);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_JSON, resp.getHeaders().getContentType());
        assertEquals("Password reset email sent successfully.", resp.getBody().get("message"));
        verify(appUserService).generateResetToken("a@b.com");
        verify(emailService).sendResetEmail(eq("a@b.com"), contains("token123"));
    }

    @Test
    void verifyEmail_CallsService() {
        ResponseEntity<String> resp = controller.verifyEmail("tok");
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("Email verified successfully.", resp.getBody());
        verify(appUserService).verifyEmail("tok");
    }

    @Test
    void updatePassword_CallsService() {
        PasswordResetRequestDto req = new PasswordResetRequestDto();
        req.setToken("tok");
        req.setPassword("newpass123");

        ResponseEntity<Map<String, String>> resp = controller.updatePassword(req);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("Password reset successfully.", resp.getBody().get("message"));
        verify(appUserService).verifyPasswordReset("tok", "newpass123");
    }

    @Test
    void softDeleteUsers_CallsService() {
        List<Long> ids = List.of(1L, 2L);
        ResponseEntity<Void> resp = controller.softDeleteUsers(ids);
        assertEquals(200, resp.getStatusCodeValue());
        verify(appUserService).softDeleteUsers(ids);
    }

    @Test
    void anonymiseSelf_ParsesTokenAndCallsService() throws Exception {
       
        String secret = "NeedANew32CharacterOrMoreSecretKeyHere";
        // Build a claims set with the userId
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("userId", 77L)
                // add a future expiry so it isn’t considered expired
                .expirationTime(new Date(System.currentTimeMillis() + 60_000))
                .build();
        // Create and sign the JWT with HS256
        SignedJWT jwt = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claims
        );
        jwt.sign(new MACSigner(secret.getBytes()));

        String token = jwt.serialize();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        ResponseEntity<Void> resp = controller.anonymiseSelf(request);

        assertEquals(204, resp.getStatusCodeValue());
        verify(appUserService).anonymiseUserById(77L);
    }
}
