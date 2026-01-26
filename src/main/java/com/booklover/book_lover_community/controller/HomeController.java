package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.service.BookService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final BookService bookService;

    // Konstruktor do wstrzyknięcia BookService
    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    // Obsługa strony głównej aplikacji
    @GetMapping("/home")
    public String homeRedirect(
            Authentication authentication,
            Model model,
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "title") String type
    ) {

        // Sprawdzenie czy użytkownik jest zalogowany jako ADMIN
        // jeśli tak, to przekierowanie do panelu admina
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    return "redirect:/admin";
                }
            }
        }

        // Pobranie listy książek:
        // jeśli nie ma wyszukiwania → losowe
        // jeśli jest → według tytułu lub autora
        List<Book> books = bookService.searchBooks(query, type);

        // Przekazanie danych do widoku
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        model.addAttribute("type", type);

        // Zwrócenie widoku home.html
        return "home";
    }
}
