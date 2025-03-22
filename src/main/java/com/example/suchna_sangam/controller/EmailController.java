package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.EmailRequest;
import com.example.suchna_sangam.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/email"})
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping({"/send"})
    public ResponseEntity<String> sendEmail(@RequestParam String subject, @RequestParam String message, @RequestParam String senderEmail) {
        EmailRequest emailRequest = new EmailRequest("onboarding@resend.dev", "cloudnexus@googlegroups.com", subject, message, senderEmail);
        boolean success = this.emailService.sendEmail(emailRequest);
        return success ? ResponseEntity.ok("Email sent successfully!") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email.");
    }
}