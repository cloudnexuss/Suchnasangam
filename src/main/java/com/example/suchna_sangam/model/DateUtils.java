package com.example.suchna_sangam.model;

import com.google.cloud.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            Instant instant = timestamp.toDate().toInstant();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' HH:mm:ss z", Locale.ENGLISH).withZone(ZoneId.of("Asia/Kolkata"));
            return formatter.format(instant);
        }
    }
}