package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.Grievance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrievanceService {
    private static final Logger LOGGER = Logger.getLogger(GrievanceService.class.getName());
    private final FirebaseDatabase firebaseDatabase;

    @Autowired
    public GrievanceService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public String submitGrievance(Grievance grievance, String districtId) {
        DatabaseReference grievancesRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances");
        String grievanceId = grievancesRef.push().getKey();
        if (grievanceId == null) {
            LOGGER.severe("Failed to generate grievance ID");
            return null;
        } else {
            grievance.setId(grievanceId);
            if (grievance.getStatus() == null) {
                grievance.setStatus("pending");
            }

            if (grievance.getTimestamp() == 0L) {
                grievance.setTimestamp(System.currentTimeMillis());
            }

            grievancesRef.child(grievanceId).setValueAsync(grievance);
            LOGGER.info("Grievance submitted successfully with ID: " + grievanceId);
            return grievanceId;
        }
    }

    public CompletableFuture<List<Grievance>> getGrievancesByOperator(String districtId, String operatorId) {
        final CompletableFuture<List<Grievance>> future = new CompletableFuture();
        DatabaseReference grievancesRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances");
        LOGGER.info("Fetching grievances for district: " + districtId + " and operator: " + operatorId);
        grievancesRef.orderByChild("operatorId").equalTo(operatorId).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Grievance> grievances = new ArrayList();
                Iterator var3 = dataSnapshot.getChildren().iterator();

                while(var3.hasNext()) {
                    DataSnapshot snapshot = (DataSnapshot)var3.next();
                    Grievance grievance = (Grievance)snapshot.getValue(Grievance.class);
                    if (grievance != null) {
                        grievances.add(grievance);
                        GrievanceService.LOGGER.info("Fetched grievance: " + String.valueOf(grievance));
                    }
                }

                future.complete(grievances);
            }

            public void onCancelled(DatabaseError databaseError) {
                GrievanceService.LOGGER.log(Level.SEVERE, "Error fetching grievances: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    public CompletableFuture<List<Grievance>> getActiveGrievancesByOperator(String districtId, String operatorId) {
        final CompletableFuture<List<Grievance>> future = new CompletableFuture();
        DatabaseReference grievancesRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances");
        LOGGER.info("Fetching active grievances for operator: " + operatorId);
        grievancesRef.orderByChild("operatorId").equalTo(operatorId).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Grievance> grievances = new ArrayList();
                Iterator var3 = dataSnapshot.getChildren().iterator();

                while(true) {
                    Grievance grievance;
                    do {
                        do {
                            if (!var3.hasNext()) {
                                future.complete(grievances);
                                return;
                            }

                            DataSnapshot snapshot = (DataSnapshot)var3.next();
                            grievance = (Grievance)snapshot.getValue(Grievance.class);
                        } while(grievance == null);
                    } while(!"pending".equals(grievance.getStatus()) && !"in-progress".equals(grievance.getStatus()));

                    grievances.add(grievance);
                }
            }

            public void onCancelled(DatabaseError databaseError) {
                GrievanceService.LOGGER.log(Level.SEVERE, "Error fetching grievances: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    public CompletableFuture<List<String>> getOperatorsWithActiveGrievances(String districtId) {
        final CompletableFuture<List<String>> future = new CompletableFuture();
        DatabaseReference grievancesRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances");
        grievancesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> operators = new ArrayList();
                Iterator var3 = dataSnapshot.getChildren().iterator();

                while(true) {
                    Grievance grievance;
                    do {
                        do {
                            if (!var3.hasNext()) {
                                future.complete(operators);
                                return;
                            }

                            DataSnapshot snapshot = (DataSnapshot)var3.next();
                            grievance = (Grievance)snapshot.getValue(Grievance.class);
                        } while(grievance == null);
                    } while(!"pending".equals(grievance.getStatus()) && !"in-progress".equals(grievance.getStatus()));

                    if (!operators.contains(grievance.getOperatorId())) {
                        operators.add(grievance.getOperatorId());
                    }
                }
            }

            public void onCancelled(DatabaseError databaseError) {
                GrievanceService.LOGGER.log(Level.SEVERE, "Error fetching operators: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    public CompletableFuture<Boolean> updateGrievanceStatus(String districtId, String grievanceId, String newStatus) {
        CompletableFuture<Boolean> future = new CompletableFuture();
        DatabaseReference grievanceRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtId + "/grievances/" + grievanceId);
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
