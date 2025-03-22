package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.EmailRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {
    private final RestTemplate restTemplate;
    private String resendApiKey = "re_DbSQaN8U_F1BSNL1iSw1ENzLEEEB3oHxM";

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean sendEmail(EmailRequest request) {
        String url = "https://api.resend.com/emails";
        HttpHeaders headers = new HttpHeaders();
        String var10002 = this.resendApiKey;
        headers.set("Authorization", "Bearer " + var10002);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Reply-To", request.getReplyTo());
        HttpEntity<EmailRequest> entity = new HttpEntity(request, headers);
        ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class, new Object[0]);
        return response.getStatusCode() == HttpStatus.OK;
    }
}
