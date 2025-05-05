package com.tuneup.tuneup.authentication.controller;

import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.dtos.LoginRequestDto;
import com.tuneup.tuneup.users.dtos.LoginResponseDto;
import com.tuneup.tuneup.users.exceptions.EmailNotVerifiedException;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.services.AppUserService;
import com.tuneup.tuneup.utils.Jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    AppUserService appUserService;

    @Autowired
    ProfileService profileService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * End point to handle authenticiation and JWT generation.
     * @param loginRequest Details of the user attempting to auth
     * @return LoginResponseDto
     * @throws Exception
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest) throws Exception {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDto userDetails = appUserService.getUserByEmail(loginRequest.getEmail());

        if (!userDetails.getVerified()) {
            throw new EmailNotVerifiedException("Email is not verified. Please verify your email before logging in.");
        }

        List<String> roles = userDetails.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();

        ProfileDto profile = profileService.getProfileDtoByUserId(userDetails.getId());
        Long profileId = profile.getId();
        ProfileType profileType = profile.getProfileType();
        // Generate JWT token
        String token = jwtUtil.generateToken(loginRequest.getEmail(),userDetails.getName(),userDetails.getId(),profileId,profileType,roles);
        return new LoginResponseDto(token);
    }

    /**
     * Logs user out, blacklists token
     * @param authHeader
     * @return logged out message
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        jwtUtil.blacklistToken(token);
        return ResponseEntity.ok("Logged out successfully.");
    }
}
