package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.News;
import com.example.suchna_sangam.service.NewsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/news"})
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping({"/{genre}"})
    public List<News> getTopNews(@PathVariable String genre) {
        return this.newsService.fetchTopNews(genre);
    }
}
