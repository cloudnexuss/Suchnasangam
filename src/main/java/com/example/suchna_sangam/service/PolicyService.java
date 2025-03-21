package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.Policy;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;


@Service
public class PolicyService {

    private static final Logger LOGGER = Logger.getLogger(PolicyService.class.getName());
    private final FirebaseDatabase firebaseDatabase;

    @Autowired
    public PolicyService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    // ✅ Optimized submitPolicy without status & timestamp
    public String submitPolicy(Policy policy) {
        LOGGER.info("Received policy submission request: " + policy);

        String districtId = policy.getDistrictId();
        if (districtId == null || districtId.isEmpty()) {
            LOGGER.severe("District ID cannot be null or empty");
            return null;
        }

        DatabaseReference policyRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/policies");
        String policyId = policyRef.push().getKey();

        if (policyId == null) {
            LOGGER.severe("Failed to generate policy ID");
            return null;
        }

        policy.setId(policyId); // Set the auto-generated ID

        // ✅ If `publishedOn` is null, set it to the current date
        if (policy.getPublishedOn() == null || policy.getPublishedOn().isEmpty()) {
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            policy.setPublishedOn(currentDate);
            LOGGER.info("Published date was missing. Set current date: " + currentDate);
        }

        // Push policy data to Firebase
        policyRef.child(policyId).setValueAsync(policy);
        LOGGER.info("Policy submitted successfully: " + policy);

        return policyId;
    }
    // ✅ Fetch all policies for a district
    public CompletableFuture<List<Policy>> getPoliciesByDistrict(String districtId) {
        if (districtId == null || districtId.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("District ID cannot be null or empty"));
        }

        DatabaseReference policyRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/policies");
        CompletableFuture<List<Policy>> future = new CompletableFuture<>();

        policyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Policy> policies = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Policy policy = data.getValue(Policy.class);
                    if (policy != null) {
                        policy.setId(data.getKey()); // Set policy ID from Firebase key
                        policies.add(policy);
                    }
                }
                future.complete(policies);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                LOGGER.severe("Error fetching policies: " + error.getMessage());
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }
}
