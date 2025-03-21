package com.example.suchna_sangam.service;


import com.example.suchna_sangam.model.Alert;
import com.example.suchna_sangam.model.AlertRequest;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class FirebaseService {

    private final FirebaseDatabase firebaseDatabase;

    public FirebaseService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    // Save a new alert to Firebase
    public void saveAlert(String districtName, AlertRequest request) {
        DatabaseReference alertsRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtName + "/alerts");

        String alertId = alertsRef.push().getKey();
        if (alertId == null) return;

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Set all operators to false (unseen)
        Map<String, Boolean> seenByMap = new HashMap<>();
        for (String operatorId : request.getOperatorIds()) {
            seenByMap.put(operatorId, false);
        }

        Alert alert = new Alert(request.getMessage(), currentDate, seenByMap);

        alertsRef.child(alertId).setValueAsync(alert);
    }

    // Fetch all alerts from Firebase
    public List<Alert> getAlerts(String districtName) {
        DatabaseReference alertsRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtName + "/alerts");
        List<Alert> alerts = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        alertsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot alertSnapshot : snapshot.getChildren()) {
                    Alert alert = alertSnapshot.getValue(Alert.class);
                    if (alert != null) {
                        alerts.add(alert);
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return alerts;
    }

    // Mark alerts as seen in Firebase
    public void markAlertsAsSeen(String districtName, String operatorId) {
        DatabaseReference alertsRef = firebaseDatabase.getReference("suchna_sangam/districts/" + districtName + "/alerts");

        alertsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot alertSnapshot : snapshot.getChildren()) {
                    alertSnapshot.child("seenBy").child(operatorId).getRef().setValueAsync(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Failed to update seenBy: " + error.getMessage());
            }
        });
    }

    // Check if an operator has unseen alerts
    public boolean hasUnseenAlerts(String districtName, String operatorId) {
        List<Alert> alerts = getAlerts(districtName);

        for (Alert alert : alerts) {
            if (alert.getSeenBy().containsKey(operatorId) && !alert.getSeenBy().get(operatorId)) {
                return true; // Operator has at least one unseen alert
            }
        }
        return false;
    }
}
