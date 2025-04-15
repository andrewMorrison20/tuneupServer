package com.tuneup.tuneup.junit;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tuneup.tuneup.securtiy.JwtRequestFilter;
import com.tuneup.tuneup.utils.Jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    private TestableJwtRequestFilter filter;

    class TestableJwtRequestFilter extends JwtRequestFilter {
        public TestableJwtRequestFilter(JwtUtil jwtUtil) {
            super(jwtUtil);
        }
        public void publicDoFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {
            super.doFilterInternal(request, response, chain);
        }
    }

    private String generateValidToken(String username, List<String> roles) throws Exception {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("username", username)
                .claim("roles", roles)
                .build();
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        MACSigner signer = new MACSigner("01234567890123456789012345678901");
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldSkipForLoginPath() throws ServletException, IOException {
        filter = new TestableJwtRequestFilter(jwtUtil);
        when(request.getServletPath()).thenReturn("/login");
        filter.publicDoFilterInternal(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldSkipForPublicPath() throws ServletException, IOException {
        filter = new TestableJwtRequestFilter(jwtUtil);
        when(request.getServletPath()).thenReturn("/public/info");
        filter.publicDoFilterInternal(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_noAuthorizationHeader() throws ServletException, IOException {
        filter = new TestableJwtRequestFilter(jwtUtil);
        when(request.getServletPath()).thenReturn("/api/resource");
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.publicDoFilterInternal(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void doFilterInternal_validToken_shouldSetAuthentication() throws Exception {
        filter = new TestableJwtRequestFilter(jwtUtil);
        when(request.getServletPath()).thenReturn("/api/resource");
        String validToken = generateValidToken("testUser", List.of("USER"));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.isTokenBlacklisted(validToken)).thenReturn(false);
        filter.publicDoFilterInternal(request, response, chain);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("testUser", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_shouldSendError() throws Exception {
        filter = new TestableJwtRequestFilter(jwtUtil);
        when(request.getServletPath()).thenReturn("/api/resource");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        filter.publicDoFilterInternal(request, response, chain);
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        verify(chain, never()).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void doFilterInternal_blacklistedToken_shouldNotSetAuthentication() throws Exception {
        filter = new TestableJwtRequestFilter(jwtUtil);
        when(request.getServletPath()).thenReturn("/api/resource");
        String validToken = generateValidToken("testUser", List.of("USER"));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.isTokenBlacklisted(validToken)).thenReturn(true);
        filter.publicDoFilterInternal(request, response, chain);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(chain, times(1)).doFilter(request, response);
    }
}
