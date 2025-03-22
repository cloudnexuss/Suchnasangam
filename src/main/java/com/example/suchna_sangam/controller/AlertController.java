package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.Alert;
import com.example.suchna_sangam.model.AlertRequest;
import com.example.suchna_sangam.service.AlertService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/alerts"})
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping({"/add"})
    public String addAlert(@RequestBody AlertRequest request) {
        this.alertService.addAlert(request);
        return "Alert added successfully";
    }

    @GetMapping({"/{districtName}"})
    public List<Alert> getAlerts(@PathVariable String districtName) {
        return this.alertService.getAlerts(districtName);
    }

    @PutMapping({"/{districtName}/seen/{operatorId}"})
    public String markAlertsAsSeen(@PathVariable String districtName, @PathVariable String operatorId) {
        this.alertService.markAlertsAsSeen(districtName, operatorId);
        return "Alerts marked as seen";
    }

    @GetMapping({"/{districtName}/unseen/{operatorId}"})
    public boolean hasUnseenAlerts(@PathVariable String districtName, @PathVariable String operatorId) {
        return this.alertService.hasUnseenAlerts(districtName, operatorId);
    }
}
