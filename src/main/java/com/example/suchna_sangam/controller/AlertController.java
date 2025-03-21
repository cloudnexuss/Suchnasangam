package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.Alert;
import com.example.suchna_sangam.model.AlertRequest;
import com.example.suchna_sangam.service.AlertService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // Bureaucrat submits a new alert
    @PostMapping("/add")
    public String addAlert(@RequestBody AlertRequest request) {
        alertService.addAlert(request);
        return "Alert added successfully";
    }

    // Operator fetches alerts for their district (sorted by latest)
    @GetMapping("/{districtName}")
    public List<Alert> getAlerts(@PathVariable String districtName) {
        return alertService.getAlerts(districtName);
    }

    // Operator marks alerts as seen
    @PutMapping("/{districtName}/seen/{operatorId}")
    public String markAlertsAsSeen(@PathVariable String districtName, @PathVariable String operatorId) {
        alertService.markAlertsAsSeen(districtName, operatorId);
        return "Alerts marked as seen";
    }
    // Check if an operator has any unseen alerts
    @GetMapping("/{districtName}/unseen/{operatorId}")
    public boolean hasUnseenAlerts(@PathVariable String districtName, @PathVariable String operatorId) {
        return alertService.hasUnseenAlerts(districtName, operatorId);
    }


}
