package com.example.suchna_sangam.service;


import com.example.suchna_sangam.model.Alert;
import com.example.suchna_sangam.model.AlertRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {
    private final FirebaseDatabase firebaseDatabase;

    public FirebaseService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public void saveAlert(String districtName, AlertRequest request) {
        DatabaseReference alertsRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtName + "/alerts");
        String alertId = alertsRef.push().getKey();
        if (alertId != null) {
            String currentDate = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
            Map<String, Boolean> seenByMap = new HashMap();
            Iterator var7 = request.getOperatorIds().iterator();

            while(var7.hasNext()) {
                String operatorId = (String)var7.next();
                seenByMap.put(operatorId, false);
            }

            Alert alert = new Alert(request.getMessage(), currentDate, seenByMap);
            alertsRef.child(alertId).setValueAsync(alert);
        }
    }

    public List<Alert> getAlerts(String districtName) {
        DatabaseReference alertsRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtName + "/alerts");
        final List<Alert> alerts = new ArrayList();
        final CountDownLatch latch = new CountDownLatch(1);
        alertsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                Iterator var2 = snapshot.getChildren().iterator();

                while(var2.hasNext()) {
                    DataSnapshot alertSnapshot = (DataSnapshot)var2.next();
                    Alert alert = (Alert)alertSnapshot.getValue(Alert.class);
                    if (alert != null) {
                        alerts.add(alert);
                    }
                }

                latch.countDown();
            }

            public void onCancelled(DatabaseError error) {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException var6) {
            var6.printStackTrace();
        }

        return alerts;
    }

    public void markAlertsAsSeen(String districtName, String operatorId) {
        DatabaseReference alertsRef = this.firebaseDatabase.getReference("suchna_sangam/districts/" + districtName + "/alerts");
        alertsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                Iterator var2 = snapshot.getChildren().iterator();

                while(var2.hasNext()) {
                    DataSnapshot alertSnapshot = (DataSnapshot)var2.next();
                    alertSnapshot.child("seenBy").child(operatorId).getRef().setValueAsync(true);
                }

            }

            public void onCancelled(DatabaseError error) {
                System.err.println("Failed to update seenBy: " + error.getMessage());
            }
        });
    }

    public boolean hasUnseenAlerts(String districtName, String operatorId) {
        List<Alert> alerts = this.getAlerts(districtName);
        Iterator var4 = alerts.iterator();

        Alert alert;
        do {
            if (!var4.hasNext()) {
                return false;
            }

            alert = (Alert)var4.next();
        } while(!alert.getSeenBy().containsKey(operatorId) || (Boolean)alert.getSeenBy().get(operatorId));

        return true;
    }
}