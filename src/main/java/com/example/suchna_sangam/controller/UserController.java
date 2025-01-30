package com.example.suchna_sangam.controller;


import com.example.suchna_sangam.model.LoginRequest;
import com.example.suchna_sangam.model.SignupRequest;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final Firestore firestore;

    public UserController() {
        firestore = FirestoreClient.getFirestore();
    }

    // User signup logic
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        try {
            // Create user with Firebase Authentication
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(signupRequest.getEmail())
                    .setPassword(signupRequest.getPassword());
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

            // Save user details in Firestore
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", signupRequest.getName());
            userData.put("email", signupRequest.getEmail());
            userData.put("role", signupRequest.getRole());
            userData.put("createdAt", Instant.now().toString());
            firestore.collection("users").document(userRecord.getUid()).set(userData);

            return ResponseEntity.ok("User signed up successfully.");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // User login logic
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Verify the user's email and password using Firebase REST API
            String firebaseAuthUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyAglVSe0BPl__DNryAArCs-sXsWq1LNNQE";
            Map<String, String> payload = new HashMap<>();
            payload.put("email", loginRequest.getEmail());
            payload.put("password", loginRequest.getPassword());
            payload.put("returnSecureToken", "true");

            // Make an HTTP POST request to Firebase REST API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> firebaseResponse = restTemplate.postForEntity(firebaseAuthUrl, payload, Map.class);

            if (firebaseResponse.getStatusCode() == HttpStatus.OK) {
                // Login successful, fetch user details from Firestore
                var userSnapshot = firestore.collection("users")
                        .whereEqualTo("email", loginRequest.getEmail())
                        .get()
                        .get();

                if (userSnapshot.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User data not found."));
                }

                var userDocument = userSnapshot.getDocuments().get(0);
                var userData = userDocument.getData();

                // Construct the response
                String role = (String) userData.getOrDefault("role", "User");
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful.");
                response.put("role", role);
                response.put("data", Map.of(
                        "userId", userDocument.getId(),
                        "email", loginRequest.getEmail()
                ));

                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error logging in: " + e.getMessage()));
        }
    }


    // Welcome endpoint
    @GetMapping("/")
    public String welcome() {
        return "Welcome to Suchna Sangam!";
    }
}
