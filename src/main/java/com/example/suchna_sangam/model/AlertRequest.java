package com.example.suchna_sangam.model;

import java.util.List;

public class AlertRequest {
    private String message;
    private String districtName; // Required to store alert under the district
    private List<String> operatorIds; // List of operator IDs in that district

    // Constructors
    public AlertRequest() {}

    public AlertRequest(String message, String districtName, List<String> operatorIds) {
        this.message = message;
        this.districtName = districtName;
        this.operatorIds = operatorIds;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public List<String> getOperatorIds() {
        return operatorIds;
    }

    public void setOperatorIds(List<String> operatorIds) {
        this.operatorIds = operatorIds;
    }
}
