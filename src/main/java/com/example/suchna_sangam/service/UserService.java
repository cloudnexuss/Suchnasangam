package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.DateUtils;
import com.example.suchna_sangam.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    public static User getUserById(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot document = db.collection("users").document(userId).get().get();

        if (document.exists()) {
            User user = new User();
            user.setId(userId);
            user.setName(document.getString("name"));
            user.setEmail(document.getString("email"));
            user.setDistrictId(document.getString("district_id"));
            user.setRole(document.getString("role"));
            user.setCreatedAt(document.getString("created_at"));
            user.setCircle(document.getString("circle"));
            user.setLastLogin(document.getTimestamp("lastLogin").toString());
            return user;
        } else {
            return null;
        }
    }

    public List<User> getOperatorsByDistrict(String districtId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference usersRef = db.collection("users");

        // Query for operators in the given district
        Query query = usersRef.whereEqualTo("district_id", districtId)
                .whereEqualTo("role", "operator");

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<User> operators = new ArrayList<>();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            User operator = new User();
            operator.setId(document.getId());
            operator.setName(document.getString("name"));
            operator.setEmail(document.getString("email"));
            operator.setDistrictId(document.getString("district_id"));
            operator.setRole(document.getString("role"));
            operator.setCreatedAt(document.getString("created_at"));
            operator.setCircle(document.getString("circle"));
            operator.setLastLogin(DateUtils.formatTimestamp(document.getTimestamp("lastLogin")));
            operators.add(operator);
        }

        return operators;
    }

    public List<String> getOperatorIdsByDistrict(String districtId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference usersRef = db.collection("users");

        // Query for operator IDs in the given district
        Query query = usersRef.whereEqualTo("district_id", districtId)
                .whereEqualTo("role", "operator");

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<String> operatorIds = new ArrayList<>();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            operatorIds.add(document.getId());  // Only store operator IDs
        }

        return operatorIds;
    }

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference usersRef = db.collection("users");

        Query query = usersRef.whereEqualTo("email", email);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        if (!documents.isEmpty()) {
            DocumentSnapshot document = documents.get(0);
            User user = new User();
            user.setId(document.getId());
            user.setName(document.getString("name"));
            user.setEmail(document.getString("email"));
            user.setDistrictId(document.getString("district_id"));
            user.setRole(document.getString("role"));
            user.setCreatedAt(document.getString("created_at"));
            user.setCircle(document.getString("circle"));
            user.setLastLogin(DateUtils.formatTimestamp(document.getTimestamp("lastLogin")));
            return user;
        }
        return null;
    }

    public boolean updatePassword(String email, String newPassword) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        User user = getUserByEmail(email);

        if (user != null) {
            try {
                // Step 1: Update password in Firebase Authentication
                UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
                UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(userRecord.getUid())
                        .setPassword(newPassword);

                FirebaseAuth.getInstance().updateUser(request);

                // Step 2: Update password in Firestore
                DocumentReference docRef = db.collection("users").document(user.getId());
                docRef.update("password", newPassword);

                return true;
            } catch (FirebaseAuthException e) {
                e.printStackTrace();
                return false; // Firebase Authentication update failed
            }
        }
        return false; // User not found
    }
}

