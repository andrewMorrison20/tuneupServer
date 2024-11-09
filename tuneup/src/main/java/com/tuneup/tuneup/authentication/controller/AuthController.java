package com.tuneup.tuneup.authentication.controller;



import com.nimbusds.jose.JOSEException;
import com.tuneup.tuneup.users.dtos.LoginRequestDto;
import com.tuneup.tuneup.users.dtos.LoginResponseDto;
import com.tuneup.tuneup.users.AppUser;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.utils.Jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Endpoint to handle login and generate JWT
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest) throws JOSEException {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        return new LoginResponseDto(token);
    }

}
