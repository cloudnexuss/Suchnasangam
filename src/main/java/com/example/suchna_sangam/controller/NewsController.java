package com.example.suchna_sangam.controller;

import com.example.suchna_sangam.model.News;
import com.example.suchna_sangam.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/{genre}")
    public List<News> getTopNews(@PathVariable String genre)  {
        return newsService.fetchTopNews(genre);
    }
}