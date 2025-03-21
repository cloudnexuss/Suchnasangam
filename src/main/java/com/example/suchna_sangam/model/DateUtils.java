package com.example.suchna_sangam.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import com.google.cloud.Timestamp;

public class DateUtils {
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        Instant instant = timestamp.toDate().toInstant();  // Correct way to get Instant
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' HH:mm:ss z", Locale.ENGLISH)
                .withZone(ZoneId.of("Asia/Kolkata")); // UTC+5:30

        return formatter.format(instant);
    }
}
