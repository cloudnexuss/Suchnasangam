package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.DateUtils;
import com.example.suchna_sangam.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.google.firebase.cloud.FirestoreClient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public static User getUserById(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot document = (DocumentSnapshot)db.collection("users").document(userId).get().get();
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
        Query query = usersRef.whereEqualTo("district_id", districtId).whereEqualTo("role", "operator");
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<User> operators = new ArrayList();
        Iterator var7 = ((QuerySnapshot)querySnapshot.get()).getDocuments().iterator();

        while(var7.hasNext()) {
            DocumentSnapshot document = (DocumentSnapshot)var7.next();
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
        Query query = usersRef.whereEqualTo("district_id", districtId).whereEqualTo("role", "operator");
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<String> operatorIds = new ArrayList();
        Iterator var7 = ((QuerySnapshot)querySnapshot.get()).getDocuments().iterator();

        while(var7.hasNext()) {
            DocumentSnapshot document = (DocumentSnapshot)var7.next();
            operatorIds.add(document.getId());
        }

        return operatorIds;
    }

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("email", email);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = ((QuerySnapshot)querySnapshot.get()).getDocuments();
        if (!documents.isEmpty()) {
            DocumentSnapshot document = (DocumentSnapshot)documents.get(0);
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
        } else {
            return null;
        }
    }

    public boolean updatePassword(String email, String newPassword) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        User user = this.getUserByEmail(email);
        if (user != null) {
            try {
                UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
                UpdateRequest request = (new UpdateRequest(userRecord.getUid())).setPassword(newPassword);
                FirebaseAuth.getInstance().updateUser(request);
                DocumentReference docRef = db.collection("users").document(user.getId());
                docRef.update("password", newPassword, new Object[0]);
                return true;
            } catch (FirebaseAuthException var8) {
                var8.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
