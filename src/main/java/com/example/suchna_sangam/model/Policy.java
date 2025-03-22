package com.example.suchna_sangam.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown fields from Firebase
public class Policy {
    private String id;
    private String subject;
    private String description;
    private String bureaucratId;
    private String districtId;

    @JsonProperty("publishedOn")  // Ensures JSON mapping
    private String publishedOn;

}
