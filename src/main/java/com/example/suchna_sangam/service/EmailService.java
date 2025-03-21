package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.EmailRequest;
import org.springframework.http.MediaType;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    private final RestTemplate restTemplate;

    private String resendApiKey="re_DbSQaN8U_F1BSNL1iSw1ENzLEEEB3oHxM";

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean sendEmail(EmailRequest request) {
        String url = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + resendApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Reply-To", request.getReplyTo());

        HttpEntity<EmailRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }
}
