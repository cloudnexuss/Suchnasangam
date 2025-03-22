package com.example.suchna_sangam.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FirebaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void initialize() throws IOException {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing Firebase...");
                FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");
                FirebaseOptions options = (new Builder()).setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl("https://suchna-sangam-default-rtdb.firebaseio.com/").build();
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
            }

        } catch (Exception var3) {
            logger.error("Failed to initialize Firebase: ", var3);
            throw new RuntimeException("Firebase initialization failed", var3);
        }
    }

    @Bean
    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Bean
    public FirebaseDatabase firebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    @Configuration
    public class AppConfig {
        public AppConfig(final FirebaseConfig this$0) {
        }

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}
