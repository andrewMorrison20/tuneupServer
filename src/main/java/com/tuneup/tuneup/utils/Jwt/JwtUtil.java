package com.tuneup.tuneup.utils.Jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {


    private String secret = "NeedANew32CharacterOrMoreSecretKeyHere";

    public String generateToken(String username) throws JOSEException {

        JWSSigner signer = new MACSigner(secret);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("User Details")
                .claim("username", username)
                .issuer("TUNEUP")
                .issueTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public String validateTokenAndRetrieveSubject(String token) throws Exception {

        SignedJWT signedJWT = SignedJWT.parse(token);


        JWSVerifier verifier = new MACVerifier(secret);


        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Invalid token signature");
        }

        return signedJWT.getJWTClaimsSet().getStringClaim("username");
    }
}
