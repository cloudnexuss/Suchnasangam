package com.example.suchna_sangam.service;


import com.example.suchna_sangam.model.Alert;
import com.example.suchna_sangam.model.AlertRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
    private final FirebaseService firebaseService;

    public AlertService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public void addAlert(AlertRequest request) {
        this.firebaseService.saveAlert(request.getDistrictName(), request);
    }

    public List<Alert> getAlerts(String districtName) {
        List<Alert> alerts = this.firebaseService.getAlerts(districtName);
        alerts.sort((a1, a2) -> {
            return a2.getPublishedOn().compareTo(a1.getPublishedOn());
        });
        return alerts;
    }

    public void markAlertsAsSeen(String districtName, String operatorId) {
        this.firebaseService.markAlertsAsSeen(districtName, operatorId);
    }

    public boolean hasUnseenAlerts(String districtName, String operatorId) {
        return this.firebaseService.hasUnseenAlerts(districtName, operatorId);
    }
}
