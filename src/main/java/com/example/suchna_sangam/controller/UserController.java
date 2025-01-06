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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
            // Attempt to sign in using Firebase Authentication
            FirebaseAuth.getInstance().getUserByEmail(loginRequest.getEmail());

            // Fetch user details from Firestore
            var userSnapshot = firestore.collection("users")
                    .whereEqualTo("email", loginRequest.getEmail())
                    .get()
                    .get();

            if (userSnapshot.isEmpty()) {
                // Return unauthorized response if no user is found
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password."));
            }

            // Assuming email uniqueness, fetch the first document
            var userDocument = userSnapshot.getDocuments().get(0);
            var userData = userDocument.getData();

            if (userData == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user data."));
            }

            // Extract the user role
            String role = (String) userData.getOrDefault("role", "User");

            // Construct the response for the "Operator" role with redirect URL
            if ("Operator".equalsIgnoreCase(role)) {
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful. Redirecting to Unsplash...",
                        "redirectUrl", "https://unsplash.com",
                        "role", role,
                        "data", Map.of(
                                "userId", userDocument.getId(),
                                "email", loginRequest.getEmail()
                        )
                ));
            }

            // For other roles, return a generic login success message and user details
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful.");
            response.put("role", role);
            response.put("data", Map.of(
                    "userId", userDocument.getId(),
                    "email", loginRequest.getEmail()
            ));

            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password."));
        } catch (InterruptedException | ExecutionException e) {
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
