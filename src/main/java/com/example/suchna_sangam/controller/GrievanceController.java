package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.Grievance;
import com.example.suchna_sangam.service.GrievanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
@RestController
@RequestMapping("/grievances")
public class GrievanceController {

    private static final Logger LOGGER = Logger.getLogger(GrievanceController.class.getName());

    @Autowired
    private GrievanceService grievanceService;

    @PostMapping("/{districtId}")
    public ResponseEntity<String> submitGrievance(@PathVariable String districtId, @RequestBody Grievance grievance) {
        String grievanceId = grievanceService.submitGrievance(grievance, districtId);
        return ResponseEntity.ok("Grievance submitted with ID: " + grievanceId);
    }

    @GetMapping("/{districtId}/pending-operators")
    public CompletableFuture<ResponseEntity<List<String>>> getOperatorsWithPendingGrievances(@PathVariable String districtId) {
        return grievanceService.getOperatorsWithActiveGrievances(districtId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> {
                    LOGGER.log(Level.SEVERE, "Error fetching operators with pending grievances", e);
                    return ResponseEntity.internalServerError().body(List.of());
                });
    }
    @GetMapping("/{districtId}/operator/{operatorId}")
    public CompletableFuture<ResponseEntity<List<Grievance>>> getGrievancesByOperator(@PathVariable String districtId, @PathVariable String operatorId) {
        return grievanceService.getActiveGrievancesByOperator(districtId, operatorId)
                .thenApply(grievances -> ResponseEntity.ok(grievances))
                .exceptionally(e -> {
                    LOGGER.log(Level.SEVERE, "Error fetching grievances for operator: " + operatorId, e);
                    return ResponseEntity.internalServerError().build(); // Correct return type
                });
    }

    @GetMapping("/{districtId}/operator/{operatorId}/history")
    public CompletableFuture<ResponseEntity<List<Grievance>>> getOperatorGrievanceHistory(@PathVariable String districtId, @PathVariable String operatorId) {
        return grievanceService.getGrievancesByOperator(districtId, operatorId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> {
                    LOGGER.log(Level.SEVERE, "Error fetching grievance history for operator: " + operatorId);
                    return ResponseEntity.internalServerError().body(List.of());
                });
    }
    @PatchMapping("/{districtId}/update-status/{grievanceId}")
    public CompletableFuture<ResponseEntity<String>> updateGrievanceStatus(
            @PathVariable String districtId,
            @PathVariable String grievanceId,
            @RequestBody GrievanceStatusRequest request) {  // ✅ Expect JSON request body
        String newStatus = request.getNewStatus().toLowerCase();  // ✅ Ensure case consistency

        if (!newStatus.equals("in-progress") && !newStatus.equals("resolved")) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid status update."));
        }

        return grievanceService.updateGrievanceStatus(districtId, grievanceId, newStatus)
                .thenApply(success -> success ? ResponseEntity.ok("Grievance status updated successfully.")
                        : ResponseEntity.internalServerError().body("Failed to update grievance status."))
                .exceptionally(e -> {
                    LOGGER.log(Level.SEVERE, "Error updating grievance status", e);
                    return ResponseEntity.internalServerError().body("An error occurred.");
                });
    }

    // ✅ Helper class to handle JSON request for status update
    public static class GrievanceStatusRequest {
        private String newStatus;

        public String getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }
    }
}
