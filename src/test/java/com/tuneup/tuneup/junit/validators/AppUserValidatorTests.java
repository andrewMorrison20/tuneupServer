package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.validators.AppUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserValidatorTests {

    @Mock
    private AppUserRepository repo;

    @InjectMocks
    private AppUserValidator validator;

    private AppUserDto dto;

    @BeforeEach
    void setUp() {
        dto = new AppUserDto();
        dto.setId(null);
        dto.setUsername("newuser");
        dto.setPassword("strongPass");
        dto.setEmail("user@example.com");
    }

    @Test
    void validateAppUserCreation_AllValid_DoesNotThrow() {
        when(repo.existsByUsername("newuser")).thenReturn(false);
        when(repo.existsByEmail("user@example.com")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateAppUserCreation(dto));

        verify(repo).existsByUsername("newuser");
        verify(repo).existsByEmail("user@example.com");
    }

    @Test
    void checkAppUserId_NullId_DoesNotThrow() {
        assertDoesNotThrow(() -> validator.checkAppUserId(null));
    }

    @Test
    void checkAppUserId_ExistingId_Throws() {
        when(repo.existsById(5L)).thenReturn(true);
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.checkAppUserId(5L)
        );
        assertEquals("App user already exists", ex.getMessage());
    }

    @Test
    void checkUsername_NullUsername_DoesNotThrow() {
        assertDoesNotThrow(() -> validator.checkUsername(null));
    }

    @Test
    void checkUsername_AlreadyExists_Throws() {
        when(repo.existsByUsername("taken")).thenReturn(true);
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.checkUsername("taken")
        );
        assertEquals("Username already exists", ex.getMessage());
    }

    @Test
    void checkPassword_Null_Throws() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.checkPassword(null)
        );
        assertEquals("Password must be at least 8 characters long", ex.getMessage());
    }

    @Test
    void checkPassword_TooShort_Throws() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.checkPassword("short")
        );
        assertEquals("Password must be at least 8 characters long", ex.getMessage());
    }

    @Test
    void checkPassword_Valid_DoesNotThrow() {
        assertDoesNotThrow(() -> validator.checkPassword("12345678"));
    }

    @Test
    void checkEmail_Null_Throws() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.checkEmail(null)
        );
        assertEquals("Invalid email format", ex.getMessage());
    }

    @Test
    void checkEmail_InvalidFormat_Throws() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.checkEmail("bad-email")
        );
        assertEquals("Invalid email format", ex.getMessage());
    }

    @Test
    void checkEmail_AlreadyExists_Throws() {
        when(repo.existsByEmail("user@example.com")).thenReturn(true);
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.checkEmail("user@example.com")
        );
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void checkEmail_Valid_DoesNotThrow() {
        when(repo.existsByEmail("ok@example.com")).thenReturn(false);
        assertDoesNotThrow(() -> validator.checkEmail("ok@example.com"));
    }
}
