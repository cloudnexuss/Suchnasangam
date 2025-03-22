package com.example.suchna_sangam.model;


import java.util.Map;

public class Alert {
    private String message;
    private String publishedOn;
    private Map<String, Boolean> seenBy;

    public String getMessage() {
        return this.message;
    }

    public String getPublishedOn() {
        return this.publishedOn;
    }

    public Map<String, Boolean> getSeenBy() {
        return this.seenBy;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setPublishedOn(final String publishedOn) {
        this.publishedOn = publishedOn;
    }

    public void setSeenBy(final Map<String, Boolean> seenBy) {
        this.seenBy = seenBy;
    }

    public Alert() {
    }

    public Alert(final String message, final String publishedOn, final Map<String, Boolean> seenBy) {
        this.message = message;
        this.publishedOn = publishedOn;
        this.seenBy = seenBy;
    }
}
