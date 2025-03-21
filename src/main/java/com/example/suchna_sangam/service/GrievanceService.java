package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.Grievance;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class GrievanceService {

    private static final Logger LOGGER = Logger.getLogger(GrievanceService.class.getName());
    private final FirebaseDatabase firebaseDatabase;

    @Autowired
    public GrievanceService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    // ✅ Submitting a Grievance
    public String submitGrievance(Grievance grievance, String districtId) {
        DatabaseReference grievancesRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances");
        String grievanceId = grievancesRef.push().getKey();

        if (grievanceId == null) {
            LOGGER.severe("Failed to generate grievance ID");
            return null;
        }

        // Ensure grievance ID is set
        grievance.setId(grievanceId);

        // Set default values if missing
        if (grievance.getStatus() == null) {
            grievance.setStatus("pending");
        }
        if (grievance.getTimestamp() == 0) {
            grievance.setTimestamp(System.currentTimeMillis());
        }

        // Push data to Firebase
        grievancesRef.child(grievanceId).setValueAsync(grievance);
        LOGGER.info("Grievance submitted successfully with ID: " + grievanceId);
        return grievanceId;
    }

    // ✅ Get all grievances submitted by a specific operator
    public CompletableFuture<List<Grievance>> getGrievancesByOperator(String districtId, String operatorId) {
        CompletableFuture<List<Grievance>> future = new CompletableFuture<>();
        DatabaseReference grievancesRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances");

        LOGGER.info("Fetching grievances for district: " + districtId + " and operator: " + operatorId);

        grievancesRef.orderByChild("operatorId").equalTo(operatorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Grievance> grievances = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grievance grievance = snapshot.getValue(Grievance.class);
                    if (grievance != null) {
                        grievances.add(grievance);
                        LOGGER.info("Fetched grievance: " + grievance);
                    }
                }
                future.complete(grievances);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LOGGER.log(Level.SEVERE, "Error fetching grievances: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }

    // ✅ Get grievances by operator with Pending/In-Progress status
    public CompletableFuture<List<Grievance>> getActiveGrievancesByOperator(String districtId, String operatorId) {
        CompletableFuture<List<Grievance>> future = new CompletableFuture<>();
        DatabaseReference grievancesRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances");

        LOGGER.info("Fetching active grievances for operator: " + operatorId);

        grievancesRef.orderByChild("operatorId").equalTo(operatorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Grievance> grievances = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grievance grievance = snapshot.getValue(Grievance.class);
                    if (grievance != null && ("pending".equals(grievance.getStatus()) || "in-progress".equals(grievance.getStatus()))) {
                        grievances.add(grievance);
                    }
                }
                future.complete(grievances);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LOGGER.log(Level.SEVERE, "Error fetching grievances: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }

    // ✅ Get operators with Pending/In-Progress grievances
    public CompletableFuture<List<String>> getOperatorsWithActiveGrievances(String districtId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        DatabaseReference grievancesRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances");

        grievancesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> operators = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grievance grievance = snapshot.getValue(Grievance.class);
                    if (grievance != null && ("pending".equals(grievance.getStatus()) || "in-progress".equals(grievance.getStatus()))) {
                        if (!operators.contains(grievance.getOperatorId())) {
                            operators.add(grievance.getOperatorId());
                        }
                    }
                }
                future.complete(operators);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LOGGER.log(Level.SEVERE, "Error fetching operators: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }

    // ✅ Update grievance status
    public CompletableFuture<Boolean> updateGrievanceStatus(String districtId, String grievanceId, String newStatus) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        DatabaseReference grievanceRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances/" + grievanceId);

        grievanceRef.child("status").setValue(newStatus, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                LOGGER.log(Level.SEVERE, "Error updating grievance status: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            } else {
                LOGGER.info("Updated grievance status to: " + newStatus);
                future.complete(true);
            }
        });

        return future;
    }
}
