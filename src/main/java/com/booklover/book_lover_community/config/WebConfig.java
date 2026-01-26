package com.booklover.book_lover_community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Klasa konfiguracyjna Springa – służy do własnych ustawień Web MVC
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Metoda pozwala dodać własne mapowanie zasobów statycznych
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Ustawiamy, że każde żądanie zaczynające się od /uploads/**
        // będzie mapowane na pliki z lokalnego katalogu "uploads"
        registry.addResourceHandler("/uploads/**")
                // "file:" oznacza, że chodzi o pliki z systemu plików,
                // a nie np. z zasobów aplikacji (resources)
                .addResourceLocations("file:uploads/");
    }
}
