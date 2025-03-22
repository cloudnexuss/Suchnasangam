package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.Policy;
import com.example.suchna_sangam.service.PolicyService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/policies"})
public class PolicyController {
    private static final Logger LOGGER = Logger.getLogger(PolicyController.class.getName());
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping({"/{districtId}"})
    public ResponseEntity<String> submitPolicy(@PathVariable String districtId, @RequestBody Policy policy) {
        LOGGER.info("Received request to submit policy for district: " + districtId);
        policy.setDistrictId(districtId);
        String policyId = this.policyService.submitPolicy(policy);
        if (policyId != null) {
            LOGGER.info("Policy submitted successfully with ID: " + policyId);
            return ResponseEntity.ok("Policy submitted successfully with ID: " + policyId);
        } else {
            LOGGER.severe("Error submitting policy");
            return ResponseEntity.internalServerError().body("Error: Failed to submit policy.");
        }
    }

    @GetMapping({"/{districtId}/history"})
    public CompletableFuture<ResponseEntity<List<Policy>>> getPolicies(@PathVariable String districtId) {
        LOGGER.info("Fetching policies for district: " + districtId);
        return this.policyService.getPoliciesByDistrict(districtId).thenApply((policies) -> {
            Logger var10000 = LOGGER;
            int var10001 = policies.size();
            var10000.info("Fetched " + var10001 + " policies for district: " + districtId);
            return ResponseEntity.ok(policies);
        }).exceptionally((ex) -> {
            LOGGER.severe("Error fetching policies: " + ex.getMessage());
            return ResponseEntity.internalServerError().body((List<Policy>) null);
        });
    }
}
