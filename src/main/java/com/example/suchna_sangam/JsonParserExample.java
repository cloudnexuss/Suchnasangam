package com.example.suchna_sangam;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class JsonParserExample {
    public static void main(String[] args) {
        String jsonResponse = "{ \"articles\": [ { \"title\": \"Roundup: Highs and Lows in the World of Sports\", \"description\": \"This sports briefing covers key events...\", \"content\": \"Star guard Anthony Edwards of the Minnesota Timberwolves faced a $35,000 fine... [525 chars]\", \"url\": \"https://www.devdiscourse.com/article/sports-games/3282907-roundup-highs-and-lows-in-the-world-of-sports\" } ] }";

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        // Extracting articles array
        JsonArray articles = jsonObject.getAsJsonArray("articles");

        // Extracting content from the first article
        String content = articles.get(0).getAsJsonObject().get("content").getAsString();

        System.out.println("Content: " + content);
    }
}
