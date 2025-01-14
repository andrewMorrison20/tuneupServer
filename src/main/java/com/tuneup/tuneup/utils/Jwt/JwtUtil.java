package com.tuneup.tuneup.utils.Jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtUtil {

    private final Set<String> blacklistedTokens = new HashSet<>();
    private final String secret = "NeedANew32CharacterOrMoreSecretKeyHere"; // Use a secure, properly configured secret key!

    // Add token to the blacklist
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    // Check if the token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    // Generate a JWT token
    public String generateToken(String username, long id) throws JOSEException {
        JWSSigner signer = new MACSigner(secret);

        // Build claims for the token
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("User Details")
                .claim("username", username)
                .claim("userId", id)
                .issuer("TUNEUP")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 3600 * 1000)) // Set expiry to 1 hour
                .build();

        // Create the signed JWT
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        // Sign the token
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    // Validate the token and retrieve the subject (username)
    public String validateTokenAndRetrieveSubject(String token) throws Exception {
        // Check if the token is blacklisted
        if (isTokenBlacklisted(token)) {
            throw new SecurityException("Token is blacklisted");
        }

        SignedJWT signedJWT = SignedJWT.parse(token);

        // Verify the token signature
        JWSVerifier verifier = new MACVerifier(secret);
        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Invalid token signature");
        }

        // Check token expiration
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime != null && expirationTime.before(new Date())) {
            throw new SecurityException("Token has expired");
        }

        // Return the username claim
        return signedJWT.getJWTClaimsSet().getStringClaim("username");
    }
}
