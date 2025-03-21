package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.Policy;
import com.example.suchna_sangam.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("/policies")
public class PolicyController {

    private static final Logger LOGGER = Logger.getLogger(PolicyController.class.getName());
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    // ✅ Submit a new policy
    @PostMapping("/{districtId}")
    public ResponseEntity<String> submitPolicy(@PathVariable String districtId, @RequestBody Policy policy) {
        LOGGER.info("Received request to submit policy for district: " + districtId);
        policy.setDistrictId(districtId);

        String policyId = policyService.submitPolicy(policy);

        if (policyId != null) {
            LOGGER.info("Policy submitted successfully with ID: " + policyId);
            return ResponseEntity.ok("Policy submitted successfully with ID: " + policyId);
        } else {
            LOGGER.severe("Error submitting policy");
            return ResponseEntity.internalServerError().body("Error: Failed to submit policy.");
        }
    }
    // ✅ Get all policies for a district
    @GetMapping("/{districtId}/history")
    public CompletableFuture<ResponseEntity<List<Policy>>> getPolicies(@PathVariable String districtId) {
        LOGGER.info("Fetching policies for district: " + districtId);

        return policyService.getPoliciesByDistrict(districtId)
                .thenApply(policies -> {
                    LOGGER.info("Fetched " + policies.size() + " policies for district: " + districtId);
                    return ResponseEntity.ok(policies);
                })
                .exceptionally(ex -> {
                    LOGGER.severe("Error fetching policies: " + ex.getMessage());
                    return ResponseEntity.internalServerError().body(null);
                });
    }
}
