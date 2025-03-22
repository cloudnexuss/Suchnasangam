package com.example.suchna_sangam.service;

import com.example.suchna_sangam.model.News;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NewsService {
    private static final String API_KEY = "48c0314a066e43f22162c42a2b5ff15a";
    private static final String BASE_URL = "https://gnews.io/api/v4/top-headlines?q=%s&lang=en&country=in&apikey=48c0314a066e43f22162c42a2b5ff15a";
    private static final DateTimeFormatter FORMATTER;

    public List<News> fetchTopNews(String genre) {
        String url = String.format("https://gnews.io/api/v4/top-headlines?q=%s&lang=en&country=in&apikey=48c0314a066e43f22162c42a2b5ff15a", genre);
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = (String)restTemplate.getForObject(url, String.class, new Object[0]);
        Gson gson = new Gson();
        JsonObject jsonObject = (JsonObject)gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray articles = jsonObject.getAsJsonArray("articles");
        List<News> newsList = new ArrayList();

        for(int i = 0; i < articles.size(); ++i) {
            JsonObject article = articles.get(i).getAsJsonObject();
            String title = article.get("title").getAsString();
            String description = article.has("description") && !article.get("description").isJsonNull() ? article.get("description").getAsString() : "No description available";
            String source = article.getAsJsonObject("source").get("name").getAsString();
            String link = article.get("url").getAsString();
            String publishedAtUtc = article.get("publishedAt").getAsString();
            URI image = article.has("image") && !article.get("image").isJsonNull() ? URI.create(article.get("image").getAsString()) : null;
            LocalDateTime utcTime = LocalDateTime.parse(publishedAtUtc, DateTimeFormatter.ISO_DATE_TIME);
            ZonedDateTime istTime = utcTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
            String publishedAt = istTime.format(FORMATTER);
            newsList.add(new News(title, description, source, link, publishedAt, image));
        }

        return newsList;
    }

    static {
        FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' HH:mm:ss 'UTC+5:30'", Locale.ENGLISH);
    }
}