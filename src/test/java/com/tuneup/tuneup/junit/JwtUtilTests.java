package com.tuneup.tuneup.junit.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.utils.Jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTests {

    private JwtUtil jwtUtil;
    private final String username = "alice";
    private final String name = "Alice Smith";
    private final long userId = 42L;
    private final long profileId = 99L;
    private final ProfileType profileType = ProfileType.TUTOR;
    private final List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateToken_And_ValidateTokenAndRetrieveSubject_Success() throws Exception {
        String token = jwtUtil.generateToken(
                username, name, userId, profileId, profileType, roles
        );
        assertNotNull(token);

        // validation should return the original username
        String subject = jwtUtil.validateTokenAndRetrieveSubject(token);
        assertEquals(username, subject);
    }

    @Test
    void blacklistToken_PreventsValidation() throws Exception {
        String token = jwtUtil.generateToken(
                username, name, userId, profileId, profileType, roles
        );

        assertFalse(jwtUtil.isTokenBlacklisted(token));
        jwtUtil.blacklistToken(token);
        assertTrue(jwtUtil.isTokenBlacklisted(token));

        SecurityException ex = assertThrows(
                SecurityException.class,
                () -> jwtUtil.validateTokenAndRetrieveSubject(token)
        );
        assertEquals("Token is blacklisted", ex.getMessage());
    }

    @Test
    void validateTokenAndRetrieveSubject_InvalidSignature_ThrowsJOSEException() throws JOSEException {
        String token = jwtUtil.generateToken(
                username, name, userId, profileId, profileType, roles
        );

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts");

        String tamperedPayload = parts[1].substring(0, parts[1].length() - 1) + "x";
        String tampered = parts[0] + "." + tamperedPayload + "." + parts[2];
        Exception ex = assertThrows(Exception.class, () -> {
            jwtUtil.validateTokenAndRetrieveSubject(tampered);
        });

        assertTrue(
                ex.getMessage().toLowerCase().contains("invalid") || ex instanceof JOSEException,
                "Expected exception message to indicate invalid token signature"
        );
    }


    @Test
    void validateTokenAndRetrieveSubject_ExpiredToken_ThrowsSecurityException() throws Exception {
        // build a token that expired 1 minute ago
        String secret = "NeedANew32CharacterOrMoreSecretKeyHere";
        JWSSigner signer = new MACSigner(secret);

        JWTClaimsSet expiredClaims = new JWTClaimsSet.Builder()
                .subject("Expired")
                .claim("username", username)
                .issueTime(new Date(System.currentTimeMillis() - 3600_000))
                .expirationTime(new Date(System.currentTimeMillis() - 60_000))
                .build();

        SignedJWT expiredJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), expiredClaims);
        expiredJWT.sign(signer);
        String expiredToken = expiredJWT.serialize();

        SecurityException ex = assertThrows(
                SecurityException.class,
                () -> jwtUtil.validateTokenAndRetrieveSubject(expiredToken)
        );
        assertEquals("Token has expired", ex.getMessage());
    }
}
