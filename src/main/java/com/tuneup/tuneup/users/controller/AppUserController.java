package com.tuneup.tuneup.users.controller;

import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.users.dtos.AppUserDto;
import com.tuneup.tuneup.users.dtos.PasswordResetRequestDto;
import com.tuneup.tuneup.users.mappers.AppUserMapper;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.services.AppUserService;
import com.tuneup.tuneup.users.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private AppUserMapper appUserMapper;
    @Autowired
    private EmailService emailService;

    /**
     * Retrieve All users registered in the db, for admin use only
     * @return the set of registered users
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findall")
    public ResponseEntity<List<AppUserDto>> getAllUsers(){
        List<AppUserDto> allUsers = appUserService.findAll();
        return ResponseEntity.ok(allUsers);
    }

    /**
     * Retrieve user by their Email address
     * @param email associated email address of user account
     * @return user if exists
     */
    @GetMapping()
    public ResponseEntity<AppUserDto> getUserByEmail(@RequestParam("email") String email){
        AppUserDto user = appUserService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Retriever user account by its id
     * @param id id of the account to find
     * @return user account details
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> getUserDetails(@PathVariable("id") long id){
        AppUser user = appUserService.findById(id);
        appUserMapper.toAppUserDto(user);
        return ResponseEntity.ok(appUserMapper.toAppUserDto(user));
    }

    /**
     * Update a user
     * @param appUserDto updated details
     * @return AppUSerDto newly updated user
     */
    @PutMapping("/update")
    public ResponseEntity<AppUserDto> updateUser(@RequestBody AppUserDto appUserDto){
        AppUserDto updatedAppUserDto = appUserService.updateUser(appUserDto);
        return ResponseEntity.ok(updatedAppUserDto);
    }

    /**
     * Created a new user
     * @param appUserDto user to create
     * @param profileType type of profile
     * @return AppUSerDto newly created user
     */
    @PostMapping("/createNew")
    public ResponseEntity<AppUserDto> createUser(@RequestBody AppUserDto appUserDto,
                                                 @RequestParam("profileType") ProfileType profileType){
      AppUserDto createdUser = appUserService.createUser(appUserDto,profileType);
      return ResponseEntity.ok().body(createdUser);
    }

    /**
     * Request a new verification link for a given user by their email address
     * @param email email address of the user
     * @return success status/message
     */
    @PostMapping("/requestVerification")
    public ResponseEntity<String> requestNewVerificationLink(@RequestBody String email){
        appUserService.sendVerificationEmail(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Send a password reset link for a given user
     * @param requestBody
     * @return response message
     */
    @PostMapping("/requestResetPasswordEmail")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String token = appUserService.generateResetToken(email);
        String resetUrl = "http://localhost:4200/login/update-password?token=" + token;

        emailService.sendResetEmail(email, resetUrl);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset email sent successfully.");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /**
     * Verify an email address for an existing account
     * @param token temporary access JWT
     * @return success message
     */
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
            appUserService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully.");
    }

    /**
     * Update password fr an existing account
     * @param passwordResetRequestDto details of the reset
     * @return response messages
     */
    @PostMapping("/updatePassword")
    public ResponseEntity<Map<String,String>> updatePassword(@RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        appUserService.verifyPasswordReset(passwordResetRequestDto.getToken(), passwordResetRequestDto.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully.");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /**
     * Batch soft deletes users (this operation does not remove personal data - see anonymise)
     * @param userIds the list of ids corresponding to user accounts to soft delete
     * @return success status else throw
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/softDeleteBatch")
    public ResponseEntity<Void> softDeleteUsers(@RequestBody List<Long> userIds) {
        appUserService.softDeleteUsers(userIds);
        return ResponseEntity.ok().build();
    }

    //grabbing the id directly from req here to prevent malicious deletes by other authed users
    /**
     * Anonymises the personal data associated with a user account and then 'soft deletes'
     * @param request the request received
     * @return ok status
     * @throws ParseException
     */
    @DeleteMapping("/anonymise")
    public ResponseEntity<Void> anonymiseSelf(HttpServletRequest request) throws ParseException {
        String token = request.getHeader("Authorization").substring(7);
        SignedJWT jwt = SignedJWT.parse(token);
        Long userId = jwt.getJWTClaimsSet().getLongClaim("userId");

        appUserService.anonymiseUserById(userId);
        return ResponseEntity.noContent().build();
    }

}

