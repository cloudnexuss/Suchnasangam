package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.Grievance;
import com.example.suchna_sangam.service.GrievanceService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/grievances"})
public class GrievanceController {
    private static final Logger LOGGER = Logger.getLogger(GrievanceController.class.getName());
    @Autowired
    private GrievanceService grievanceService;

    @PostMapping({"/{districtId}"})
    public ResponseEntity<String> submitGrievance(@PathVariable String districtId, @RequestBody Grievance grievance) {
        String grievanceId = this.grievanceService.submitGrievance(grievance, districtId);
        return ResponseEntity.ok("Grievance submitted with ID: " + grievanceId);
    }

    @GetMapping({"/{districtId}/pending-operators"})
    public CompletableFuture<ResponseEntity<List<String>>> getOperatorsWithPendingGrievances(@PathVariable String districtId) {
        return this.grievanceService.getOperatorsWithActiveGrievances(districtId).thenApply(ResponseEntity::ok).exceptionally((e) -> {
            LOGGER.log(Level.SEVERE, "Error fetching operators with pending grievances", e);
            return ResponseEntity.internalServerError().body(List.of());
        });
    }

    @GetMapping({"/{districtId}/operator/{operatorId}"})
    public CompletableFuture<ResponseEntity<List<Grievance>>> getGrievancesByOperator(@PathVariable String districtId, @PathVariable String operatorId) {
        return this.grievanceService.getActiveGrievancesByOperator(districtId, operatorId).thenApply((grievances) -> {
            return ResponseEntity.ok(grievances);
        }).exceptionally((e) -> {
            LOGGER.log(Level.SEVERE, "Error fetching grievances for operator: " + operatorId, e);
            return ResponseEntity.internalServerError().build();
        });
    }

    @GetMapping({"/{districtId}/operator/{operatorId}/history"})
    public CompletableFuture<ResponseEntity<List<Grievance>>> getOperatorGrievanceHistory(@PathVariable String districtId, @PathVariable String operatorId) {
        return this.grievanceService.getGrievancesByOperator(districtId, operatorId).thenApply(ResponseEntity::ok).exceptionally((e) -> {
            LOGGER.log(Level.SEVERE, "Error fetching grievance history for operator: " + operatorId);
            return ResponseEntity.internalServerError().body(List.of());
        });
    }

    @PatchMapping({"/{districtId}/update-status/{grievanceId}"})
    public CompletableFuture<ResponseEntity<String>> updateGrievanceStatus(@PathVariable String districtId, @PathVariable String grievanceId, @RequestBody GrievanceController.GrievanceStatusRequest request) {
        String newStatus = request.getNewStatus().toLowerCase();
        return !newStatus.equals("in-progress") && !newStatus.equals("resolved") ? CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid status update.")) : this.grievanceService.updateGrievanceStatus(districtId, grievanceId, newStatus).thenApply((success) -> {
            return success ? ResponseEntity.ok("Grievance status updated successfully.") : ResponseEntity.internalServerError().body("Failed to update grievance status.");
        }).exceptionally((e) -> {
            LOGGER.log(Level.SEVERE, "Error updating grievance status", e);
            return ResponseEntity.internalServerError().body("An error occurred.");
        });
    }

    public static class GrievanceStatusRequest {
        private String newStatus;

        public String getNewStatus() {
            return this.newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }
    }
}