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

    // Konstruktor wstrzykujący BookService
    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Obsługa strony głównej /home
     * - Jeśli zalogowany użytkownik ma rolę ADMIN → przekierowanie na /admin
     * - Pobranie książek do wyświetlenia: losowe lub wyszukiwanie po tytule/autorze
     *
     * @param authentication obiekt logowania użytkownika
     * @param model          model Thymeleaf do przekazania danych
     * @param query          fraza wyszukiwania (opcjonalna)
     * @param type           typ wyszukiwania: title / author (domyślnie title)
     * @return widok home.html
     */
    @GetMapping("/home")
    public String homeRedirect(
            Authentication authentication,
            Model model,
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "title") String type
    ) {

        // Jeśli użytkownik jest adminem → przekierowanie na stronę admina
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    return "redirect:/admin";
                }
            }
        }

        // Pobranie książek: losowe lub wyszukane
        List<Book> books = bookService.searchBooks(query, type);

        // Dodanie danych do modelu
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        model.addAttribute("type", type);

        return "home"; // widok Thymeleaf
    }
}
