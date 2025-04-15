package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.address.dtos.AddressDto;
import com.tuneup.tuneup.address.services.AddressService;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.model.EmailVerificationToken;
import com.tuneup.tuneup.users.model.PasswordResetToken;
import com.tuneup.tuneup.users.repository.AppUserRepository;
import com.tuneup.tuneup.users.repository.EmailVerificationTokenRepository;
import com.tuneup.tuneup.users.repository.PasswordResetTokenRepository;
import com.tuneup.tuneup.users.services.AppUserService;
import com.tuneup.tuneup.users.services.EmailService;
import com.tuneup.tuneup.users.validators.AppUserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTests {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private AppUserMapper appUserMapper;
    @Mock
    private AppUserValidator appUserValidator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AddressService addressService;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AppUserService appUserService;


    @Test
    void testCreateUser() {
        AppUserDto inputDto = new AppUserDto();
        inputDto.setName("Test User");
        inputDto.setEmail("test@example.com");
        inputDto.setPassword("password");
        inputDto.setUsername("testuser");

        AppUser appUser = new AppUser();
        appUser.setName("Test User");
        appUser.setEmail("test@example.com");
        appUser.setPassword("password");
        appUser.setUsername("testuser");

        // When mapping from DTO to AppUser and back.
        when(appUserMapper.toAppUser(inputDto)).thenReturn(appUser);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        appUser.setId(1L);
        when(appUserRepository.save(appUser)).thenReturn(appUser);
        when(appUserMapper.toAppUserDto(appUser)).thenReturn(inputDto);

        // For the sendVerificationEmail call within createUser, ensure the repository finds the user.
        when(appUserRepository.findByEmail("test@example.com")).thenReturn(appUser);
        when(emailVerificationTokenRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Spy on service to override generateVerificationToken (used in sendVerificationEmail)
        AppUserService spyService = spy(appUserService);
        doReturn("fixedToken").when(spyService).generateVerificationToken(appUser);

        AppUserDto resultDto = spyService.createUser(inputDto, ProfileType.STUDENT);

        assertEquals(inputDto, resultDto);
        verify(appUserValidator).validateAppUserCreation(inputDto);
        verify(passwordEncoder).encode("password");
        verify(appUserRepository).save(appUser);

        // Verify that a Profile is created with the proper fields.
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();
        assertEquals(appUser, savedProfile.getAppUser());
        assertEquals("Test User", savedProfile.getDisplayName());
        assertEquals(ProfileType.STUDENT, savedProfile.getProfileType());

        // Verify that the verification email is sent with the fixed token.
        verify(emailService).sendVerificationEmail(eq("test@example.com"),
                eq("http://localhost:4200/login/verified?token=fixedToken"));
    }

    @Test
    void testSendVerificationEmail_withExistingToken() {
        String email = "test@example.com";
        AppUser user = new AppUser();
        user.setId(1L);
        user.setEmail(email);
        when(appUserRepository.findByEmail(email)).thenReturn(user);

        EmailVerificationToken existingToken = new EmailVerificationToken(user, "oldToken", LocalDateTime.now().plusHours(24));
        when(emailVerificationTokenRepository.findByUserId(1L)).thenReturn(Optional.of(existingToken));

        // Use a spy to override the token generation.
        AppUserService spyService = spy(appUserService);
        doReturn("newToken").when(spyService).generateVerificationToken(user);

        spyService.sendVerificationEmail(email);

        verify(emailVerificationTokenRepository).delete(existingToken);
        verify(emailVerificationTokenRepository).flush();
        verify(emailService).sendVerificationEmail(eq(email),
                eq("http://localhost:4200/login/verified?token=newToken"));
    }

    @Test
    void testSendVerificationEmail_withoutExistingToken() {
        String email = "test@example.com";
        AppUser user = new AppUser();
        user.setId(1L);
        user.setEmail(email);
        when(appUserRepository.findByEmail(email)).thenReturn(user);
        when(emailVerificationTokenRepository.findByUserId(1L)).thenReturn(Optional.empty());

        AppUserService spyService = spy(appUserService);
        doReturn("newToken").when(spyService).generateVerificationToken(user);

        spyService.sendVerificationEmail(email);

        verify(emailVerificationTokenRepository, never()).delete(any());
        verify(emailVerificationTokenRepository).flush();
        verify(emailService).sendVerificationEmail(eq(email),
                eq("http://localhost:4200/login/verified?token=newToken"));
    }


    @Test
    void testFindAll() {
        AppUser user1 = new AppUser();
        AppUser user2 = new AppUser();
        List<AppUser> users = List.of(user1, user2);
        when(appUserRepository.findAll()).thenReturn(users);
        AppUserDto dto1 = new AppUserDto();
        AppUserDto dto2 = new AppUserDto();
        when(appUserMapper.toAppUserDto(user1)).thenReturn(dto1);
        when(appUserMapper.toAppUserDto(user2)).thenReturn(dto2);

        List<AppUserDto> dtos = appUserService.findAll();
        assertEquals(2, dtos.size());
        assertTrue(dtos.contains(dto1));
        assertTrue(dtos.contains(dto2));
    }


    @Test
    void testFindById_valid() {
        AppUser user = new AppUser();
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
        AppUser result = appUserService.findById(1L);
        assertEquals(user, result);
    }

    @Test
    void testFindById_notFound() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> appUserService.findById(1L));
        assertTrue(exception.getMessage().contains("AppUser with ID 1 not found"));
    }


    @Test
    void testGetUserByEmail() {
        String email = "test@example.com";
        AppUser user = new AppUser();
        user.setEmail(email);
        when(appUserRepository.findByEmail(email)).thenReturn(user);
        AppUserDto dto = new AppUserDto();
        when(appUserMapper.toAppUserDto(user)).thenReturn(dto);

        AppUserDto result = appUserService.getUserByEmail(email);
        assertEquals(dto, result);
    }


    @Test
    void testUpdateUser_allFields() {
        // Existing user returned from repository.
        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setUsername("oldusername");
        existingUser.setPassword("oldPassword");

        AppUserDto existingUserDto = new AppUserDto();
        existingUserDto.setId(1L);
        existingUserDto.setName("Old Name");
        existingUserDto.setEmail("old@example.com");
        existingUserDto.setUsername("oldusername");
        existingUserDto.setPassword("oldPassword");

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(appUserMapper.toAppUserDto(existingUser)).thenReturn(existingUserDto);

        AppUserDto updateDto = new AppUserDto();
        updateDto.setId(1L);
        updateDto.setName("New Name");
        updateDto.setEmail("new@example.com");
        updateDto.setUsername("newusername");
        updateDto.setPassword("newPassword");
        // Simulate address update.
        AddressDto addressDto = new AddressDto();
        updateDto.setAddress(addressDto);

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        AddressDto updatedAddress = new AddressDto();
        when(addressService.createOrUpdateAddress(addressDto)).thenReturn(updatedAddress);

        // After changes, re-map and save.
        AppUser updatedUser = new AppUser();
        when(appUserMapper.toAppUser(existingUserDto)).thenReturn(updatedUser);
        when(appUserRepository.save(updatedUser)).thenReturn(updatedUser);
        AppUserDto updatedUserDto = new AppUserDto();
        when(appUserMapper.toAppUserDto(updatedUser)).thenReturn(updatedUserDto);

        AppUserDto result = appUserService.updateUser(updateDto);
        assertEquals(updatedUserDto, result);

        verify(appUserValidator).checkPassword("newPassword");
        verify(appUserValidator).checkUsername("newusername");
        verify(appUserValidator).checkEmail("new@example.com");
        verify(addressService).createOrUpdateAddress(addressDto);
    }

    @Test
    void testUpdateUser_noFieldUpdates() {
        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setUsername("oldusername");
        existingUser.setPassword("oldPassword");

        AppUserDto existingUserDto = new AppUserDto();
        existingUserDto.setId(1L);
        existingUserDto.setName("Old Name");
        existingUserDto.setEmail("old@example.com");
        existingUserDto.setUsername("oldusername");
        existingUserDto.setPassword("oldPassword");

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(appUserMapper.toAppUserDto(existingUser)).thenReturn(existingUserDto);

        // Update DTO with all fields null (no changes).
        AppUserDto updateDto = new AppUserDto();
        updateDto.setId(1L);

        AppUser updatedUser = new AppUser();
        when(appUserMapper.toAppUser(existingUserDto)).thenReturn(updatedUser);
        when(appUserRepository.save(updatedUser)).thenReturn(updatedUser);
        AppUserDto updatedUserDto = new AppUserDto();
        when(appUserMapper.toAppUserDto(updatedUser)).thenReturn(updatedUserDto);

        AppUserDto result = appUserService.updateUser(updateDto);
        assertEquals(updatedUserDto, result);

        verify(appUserValidator, never()).checkPassword(anyString());
        verify(appUserValidator, never()).checkUsername(anyString());
        verify(appUserValidator, never()).checkEmail(anyString());
        verify(addressService, never()).createOrUpdateAddress(any());
    }


    @Test
    void testVerifyPasswordReset_valid() {
        String token = "resetToken";
        String newPassword = "newPassword";
        AppUser user = new AppUser();
        PasswordResetToken resetToken = new PasswordResetToken(user, token, LocalDateTime.now().plusHours(1));
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        appUserService.verifyPasswordReset(token, newPassword);

        verify(appUserRepository).save(user);
        verify(passwordResetTokenRepository).delete(resetToken);
    }

    @Test
    void testVerifyPasswordReset_tokenNotFound() {
        String token = "nonexistent";
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> appUserService.verifyPasswordReset(token, "newPassword"));
        assertTrue(ex.getMessage().contains("Invalid or expired token"));
    }

    @Test
    void testVerifyPasswordReset_tokenExpired() {
        String token = "expiredToken";
        AppUser user = new AppUser();
        PasswordResetToken resetToken = new PasswordResetToken(user, token, LocalDateTime.now().minusHours(1));
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> appUserService.verifyPasswordReset(token, "newPassword"));
        assertTrue(ex.getMessage().contains("Token has expired"));
        verify(passwordResetTokenRepository, never()).delete(any());
    }


    @Test
    void testVerifyEmail_valid() {
        String token = "verifyToken";
        AppUser user = new AppUser();
        user.setVerified(false);
        EmailVerificationToken verificationToken = new EmailVerificationToken(user, token, LocalDateTime.now().plusHours(1));
        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        appUserService.verifyEmail(token);

        assertTrue(user.getVerified());
        verify(appUserRepository).save(user);
        verify(emailVerificationTokenRepository).delete(verificationToken);
    }

    @Test
    void testVerifyEmail_tokenNotFound() {
        String token = "nonexistent";
        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        Exception ex = assertThrows(ValidationException.class,
                () -> appUserService.verifyEmail(token));
        assertTrue(ex.getMessage().contains("Invalid or expired token"));
    }

    @Test
    void testVerifyEmail_tokenExpired() {
        String token = "expired";
        AppUser user = new AppUser();
        user.setVerified(false);
        EmailVerificationToken verificationToken = new EmailVerificationToken(user, token, LocalDateTime.now().minusHours(1));
        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        Exception ex = assertThrows(ValidationException.class,
                () -> appUserService.verifyEmail(token));
        assertTrue(ex.getMessage().contains("Token has expired"));
        verify(emailVerificationTokenRepository, never()).delete(any());
    }



    @Test
    void testGenerateResetToken_userExists() {
        String email = "test@example.com";
        AppUser user = new AppUser();
        when(appUserRepository.findByEmail(email)).thenReturn(user);

        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        String token = appUserService.generateResetToken(email);
        verify(passwordResetTokenRepository).save(captor.capture());
        PasswordResetToken savedToken = captor.getValue();
        assertEquals(user, savedToken.getUser());
        assertTrue(savedToken.getExpiryDate().isAfter(LocalDateTime.now()));
        assertEquals(token, savedToken.getToken());
    }

    @Test
    void testGenerateResetToken_userNotFound() {
        String email = "nonexistent@example.com";
        when(appUserRepository.findByEmail(email)).thenReturn(null);
        Exception ex = assertThrows(ValidationException.class, () -> appUserService.generateResetToken(email));
        assertTrue(ex.getMessage().contains("User with this email address, does not exist"));
    }


    @Test
    void testGenerateVerificationToken() {
        AppUser user = new AppUser();
        String token = appUserService.generateVerificationToken(user);
        ArgumentCaptor<EmailVerificationToken> captor = ArgumentCaptor.forClass(EmailVerificationToken.class);
        verify(emailVerificationTokenRepository).save(captor.capture());
        EmailVerificationToken savedToken = captor.getValue();
        assertEquals(user, savedToken.getUser());
        assertTrue(savedToken.getExpiryDate().isAfter(LocalDateTime.now()));
        assertEquals(token, savedToken.getToken());
    }


    @Test
    void testSoftDeleteUsers() {
        List<Long> userIds = List.of(1L, 2L);
        appUserService.softDeleteUsers(userIds);

        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(appUserRepository).softDeleteUsersByIds(eq(userIds), timeCaptor.capture());
        verify(profileRepository).softDeleteProfilesByUserIds(eq(userIds), eq(timeCaptor.getValue()));
        // Check that the captured time is near now.
        assertTrue(timeCaptor.getValue().isBefore(LocalDateTime.now().plusSeconds(1)));
    }


    @Test
    void testAnonymiseUserById_withProfile() {
        Long userId = 1L;
        AppUser user = new AppUser();
        user.setId(userId);
        user.setName("User Name");
        user.setEmail("user@example.com");
        user.setUsername("username");
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));

        Profile profile = new Profile();
        when(profileRepository.findByAppUserId(userId)).thenReturn(profile);

        appUserService.anonymiseUserById(userId);

        assertEquals("Deleted User", user.getName());
        assertTrue(user.getEmail().startsWith("deleted_"));
        assertTrue(user.getEmail().endsWith("@tuneup.local"));
        assertTrue(user.getUsername().startsWith("deleted_"));
        assertNull(user.getPassword());
        assertNull(user.getAddress());
        assertFalse(user.getVerified());
        assertNotNull(user.getDeletedAt());

        assertEquals("Deleted User", profile.getDisplayName());
        assertNull(profile.getBio());
        assertNull(profile.getProfilePicture());
        assertNull(profile.getInstruments());
        assertNull(profile.getPrices());
        assertNull(profile.getGenres());
        assertNull(profile.getTuitionRegion());
        assertNull(profile.getProfileInstrumentQualifications());
        assertNotNull(profile.getDeletedAt());

        verify(profileRepository).save(profile);
        verify(appUserRepository).save(user);
    }

    @Test
    void testAnonymiseUserById_withoutProfile() {
        Long userId = 2L;
        AppUser user = new AppUser();
        user.setId(userId);
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileRepository.findByAppUserId(userId)).thenReturn(null);

        appUserService.anonymiseUserById(userId);
        verify(profileRepository, never()).save(any());
        verify(appUserRepository).save(user);
    }

    @Test
    void testAnonymiseUserById_userNotFound() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(ValidationException.class, () -> appUserService.anonymiseUserById(1L));
        assertTrue(ex.getMessage().contains("User not found"));
    }
}
