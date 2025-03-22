package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.Policy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyService {
    private static final Logger LOGGER = Logger.getLogger(PolicyService.class.getName());
    private final FirebaseDatabase firebaseDatabase;

    @Autowired
    public PolicyService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public String submitPolicy(Policy policy) {
        LOGGER.info("Received policy submission request: " + String.valueOf(policy));
        String districtId = policy.getDistrictId();
        if (districtId != null && !districtId.isEmpty()) {
            DatabaseReference policyRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/policies");
            String policyId = policyRef.push().getKey();
            if (policyId == null) {
                LOGGER.severe("Failed to generate policy ID");
                return null;
            } else {
                policy.setId(policyId);
                if (policy.getPublishedOn() == null || policy.getPublishedOn().isEmpty()) {
                    String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    policy.setPublishedOn(currentDate);
                    LOGGER.info("Published date was missing. Set current date: " + currentDate);
                }

                policyRef.child(policyId).setValueAsync(policy);
                LOGGER.info("Policy submitted successfully: " + String.valueOf(policy));
                return policyId;
            }
        } else {
            LOGGER.severe("District ID cannot be null or empty");
            return null;
        }
    }

    public CompletableFuture<List<Policy>> getPoliciesByDistrict(String districtId) {
        if (districtId != null && !districtId.isEmpty()) {
            DatabaseReference policyRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/policies");
            final CompletableFuture<List<Policy>> future = new CompletableFuture();
            policyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    List<Policy> policies = new ArrayList();
                    Iterator var3 = snapshot.getChildren().iterator();

                    while(var3.hasNext()) {
                        DataSnapshot data = (DataSnapshot)var3.next();
                        Policy policy = (Policy)data.getValue(Policy.class);
                        if (policy != null) {
                            policy.setId(data.getKey());
                            policies.add(policy);
                        }
                    }

                    future.complete(policies);
                }

                public void onCancelled(DatabaseError error) {
                    PolicyService.LOGGER.severe("Error fetching policies: " + error.getMessage());
                    future.completeExceptionally(error.toException());
                }
            });
            return future;
        } else {
            return CompletableFuture.failedFuture(new IllegalArgumentException("District ID cannot be null or empty"));
        }
    }
}