package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.LoginRequest;
import com.example.suchna_sangam.model.SignupRequest;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.cloud.FirestoreClient;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping({"/api/auth"})
public class AuthController {
    private final Firestore firestore = FirestoreClient.getFirestore();

    @PostMapping({"/signup"})
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequest signupRequest) {
        try {
            CreateRequest createRequest = (new CreateRequest()).setEmail(signupRequest.getEmail()).setPassword(signupRequest.getPassword());
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
            Map<String, Object> userData = new HashMap();
            userData.put("name", signupRequest.getName());
            userData.put("email", signupRequest.getEmail());
            userData.put("role", signupRequest.getRole().toLowerCase());
            userData.put("created_at", Instant.now().toString());
            userData.put("district_id", signupRequest.getDistrict());
            userData.put("circle", signupRequest.getCircle());
            this.firestore.collection("users").document(userRecord.getUid()).set(userData);
            return ResponseEntity.ok(Map.of("message", "User signed up successfully.", "uid", userRecord.getUid()));
        } catch (FirebaseAuthException var5) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", var5.getMessage()));
        }
    }

    @PostMapping({"/login"})
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            String firebaseAuthUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyAglVSe0BPl__DNryAArCs-sXsWq1LNNQE";
            Map<String, String> payload = new HashMap();
            payload.put("email", loginRequest.getEmail());
            payload.put("password", loginRequest.getPassword());
            payload.put("returnSecureToken", "true");
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> firebaseResponse = restTemplate.postForEntity(firebaseAuthUrl, payload, Map.class, new Object[0]);
            if (firebaseResponse.getStatusCode() == HttpStatus.OK) {
                String uid = (String)((Map)firebaseResponse.getBody()).get("localId");
                DocumentReference userRef = this.firestore.collection("users").document(uid);
                DocumentSnapshot userSnapshot = (DocumentSnapshot)userRef.get().get();
                if (!userSnapshot.exists()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User data not found."));
                } else {
                    Map<String, Object> userData = userSnapshot.getData();
                    String role = (String)userData.getOrDefault("role", "user");
                    Map<String, Object> updates = new HashMap();
                    updates.put("lastLogin", FieldValue.serverTimestamp());
                    userRef.update(updates);
                    Map<String, Object> response = new HashMap();
                    response.put("message", "Login successful.");
                    response.put("role", role);
                    response.put("uid", uid);
                    response.put("email", loginRequest.getEmail());
                    return ResponseEntity.ok(response);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password."));
            }
        } catch (Exception var13) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error logging in: " + var13.getMessage()));
        }
    }

    @GetMapping({"/"})
    public String welcome() {
        return "Welcome to Suchna Sangam!";
    }
}