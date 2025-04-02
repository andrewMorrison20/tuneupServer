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
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/findall")
    public ResponseEntity<List<AppUserDto>> getAllUsers(){
        List<AppUserDto> allUsers = appUserService.findAll();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping()
    public ResponseEntity<AppUserDto> getUserByUsername(@RequestParam("email") String email){
        AppUserDto user = appUserService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<AppUserDto> getUserDetails(@RequestParam("email") String email){
        AppUserDto user = appUserService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> getUserDetails(@PathVariable("id") long id){
        AppUser user = appUserService.findById(id);
        appUserMapper.toAppUserDto(user);
        return ResponseEntity.ok(appUserMapper.toAppUserDto(user));
    }

    @PutMapping("/update")
    public ResponseEntity<AppUserDto> updateUser(@RequestBody AppUserDto appUserDto){
        AppUserDto updatedAppUserDto = appUserService.updateUser(appUserDto);
        return ResponseEntity.ok(updatedAppUserDto);
    }

    @PostMapping("/createNew")
    public ResponseEntity<AppUserDto> createUser(@RequestBody AppUserDto appUserDto,
                                                 @RequestParam("profileType") ProfileType profileType){
      AppUserDto createdUser = appUserService.createUser(appUserDto,profileType);
      return ResponseEntity.ok().body(createdUser);
    }

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

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
            appUserService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully.");
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<Map<String,String>> updatePassword(@RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        appUserService.verifyPasswordReset(passwordResetRequestDto.getToken(), passwordResetRequestDto.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully.");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}

