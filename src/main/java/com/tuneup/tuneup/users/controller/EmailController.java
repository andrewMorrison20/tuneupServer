package com.tuneup.tuneup.users.controller;

import com.tuneup.tuneup.users.dtos.EmailDto;
import com.tuneup.tuneup.users.services.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Send wa query to the admin account
     * @param emailDto email to send
     * @return success Status
     */
    @PostMapping("/contact")
    public ResponseEntity<String> sendAdminEmail(@RequestBody EmailDto emailDto) {
        emailService.sendFaqEmail(emailDto);
        return ResponseEntity.ok("Contact message sent successfully.");
    }
}
