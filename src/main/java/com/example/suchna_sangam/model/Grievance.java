package com.example.suchna_sangam.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grievance {
    private String id;
    private String subject;
    private String operatorId;
    private String operatorName;
    private String description;
    private String status; // "pending" or "resolved"
    private long timestamp;

}
