package com.example.suchna_sangam.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins(new String[]{"http://127.0.0.1:5500", "http://127.0.0.1:5501", "http://suchnasangam.s3-website.ap-south-1.amazonaws.com", "https://d3ohu1jvpua37v.cloudfront.net"}).allowedMethods(new String[]{"GET", "POST", "PATCH", "DELETE", "OPTIONS", "PUT"}).allowedHeaders(new String[]{"*"}).exposedHeaders(new String[]{"Authorization", "Content-Type"}).allowCredentials(true);
            }
        };
    }
}