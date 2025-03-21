package com.example.suchna_sangam.service;


import com.example.suchna_sangam.model.Alert;
import com.example.suchna_sangam.model.AlertRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private final FirebaseService firebaseService;

    public AlertService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    // Add a new alert
    public void addAlert(AlertRequest request) {
        firebaseService.saveAlert(request.getDistrictName(), request);
    }

    // Fetch alerts sorted by latest date
    public List<Alert> getAlerts(String districtName) {
        List<Alert> alerts = firebaseService.getAlerts(districtName);

        // Sort by latest first
        alerts.sort((a1, a2) -> a2.getPublishedOn().compareTo(a1.getPublishedOn()));

        return alerts;
    }

    // Mark alerts as seen for an operator
    public void markAlertsAsSeen(String districtName, String operatorId) {
        firebaseService.markAlertsAsSeen(districtName, operatorId);
    }

    // Check if an operator has unseen alerts
    public boolean hasUnseenAlerts(String districtName, String operatorId) {
        return firebaseService.hasUnseenAlerts(districtName, operatorId);
    }
}
