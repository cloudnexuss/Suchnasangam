package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.News;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class NewsService {
    private static final String API_KEY = "48c0314a066e43f22162c42a2b5ff15a";
    private static final String BASE_URL = "https://gnews.io/api/v4/top-headlines?q=%s&lang=en&country=in&apikey=" + API_KEY;

    // Formatter to match your required format
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' HH:mm:ss 'UTC+5:30'", Locale.ENGLISH);

    public List<News> fetchTopNews(String genre) {
        String url = String.format(BASE_URL, genre);
        RestTemplate restTemplate = new RestTemplate();

        // Fetch JSON response from API
        String jsonResponse = restTemplate.getForObject(url, String.class);

        // Parse JSON response
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray articles = jsonObject.getAsJsonArray("articles");

        List<News> newsList = new ArrayList<>();

        // Loop through JSON array and map to News object
        for (int i = 0; i < articles.size(); i++) {
            JsonObject article = articles.get(i).getAsJsonObject();

            String title = article.get("title").getAsString();
            String description = article.has("description") && !article.get("description").isJsonNull()
                    ? article.get("description").getAsString() : "No description available";
            String source = article.getAsJsonObject("source").get("name").getAsString();
            String link = article.get("url").getAsString();
            String publishedAtUtc = article.get("publishedAt").getAsString();
            URI image = article.has("image") && !article.get("image").isJsonNull()
                    ? URI.create(article.get("image").getAsString())
                    : null;
            // Convert UTC time to IST
            LocalDateTime utcTime = LocalDateTime.parse(publishedAtUtc, DateTimeFormatter.ISO_DATE_TIME);
            ZonedDateTime istTime = utcTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
            String publishedAt = istTime.format(FORMATTER);

            // Create News object and add to list
            newsList.add(new News(title, description, source, link, publishedAt,image));
        }

        return newsList;
    }
}
