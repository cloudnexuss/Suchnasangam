package com.example.suchna_sangam.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    private String from;
    private String to;
    private String subject;
    private String text;
    private String replyTo;
}
