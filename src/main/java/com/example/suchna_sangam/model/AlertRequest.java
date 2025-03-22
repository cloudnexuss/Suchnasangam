package com.example.suchna_sangam.model;

import java.util.List;

public class AlertRequest {
    private String message;
    private String districtName;
    private List<String> operatorIds;

    public AlertRequest() {
    }

    public AlertRequest(String message, String districtName, List<String> operatorIds) {
        this.message = message;
        this.districtName = districtName;
        this.operatorIds = operatorIds;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDistrictName() {
        return this.districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public List<String> getOperatorIds() {
        return this.operatorIds;
    }

    public void setOperatorIds(List<String> operatorIds) {
        this.operatorIds = operatorIds;
    }
}