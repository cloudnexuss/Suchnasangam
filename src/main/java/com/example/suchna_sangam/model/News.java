package com.example.suchna_sangam.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {
    private String title;
    private String description;
    private String source;
    private String link;
    private String publishedAt;
    private URI image;

}

