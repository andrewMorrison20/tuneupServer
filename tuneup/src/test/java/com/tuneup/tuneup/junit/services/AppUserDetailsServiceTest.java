package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.roles.services.Role;
import com.tuneup.tuneup.users.AppUser;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.services.AppUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWhenUserExists() {

        String email = "test@example.com";
        AppUser mockAppUser = new AppUser();
        mockAppUser.setEmail(email);
        mockAppUser.setPassword("password123");
        mockAppUser.setRoles(Set.of(new Role("ROLE_USER"), new Role("ROLE_ADMIN")));

        when(appUserRepository.findByEmail(email)).thenReturn(mockAppUser);

        UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));

        verify(appUserRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionWhenUserNotFound() {

        String email = "nonexistent@example.com";

        when(appUserRepository.findByEmail(email)).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> appUserDetailsService.loadUserByUsername(email));

        assertEquals("User not found", exception.getMessage());
        verify(appUserRepository).findByEmail(email);
    }
}
