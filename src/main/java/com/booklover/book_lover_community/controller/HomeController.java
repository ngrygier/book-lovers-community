package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.repository.BookRepository;
import com.booklover.book_lover_community.service.BookService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final BookRepository bookRepository;
    private final BookService bookService;

    public HomeController(BookRepository bookRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @GetMapping("/home")
    public String homeRedirect(Authentication authentication, Model model) {

        // Jeśli zalogowany użytkownik jest ADMINEM → przekieruj
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    return "redirect:/admin";
                }
            }
        }

        // Pobranie 5 losowych książek
        List<Book> randomBooks = bookService.getRandomBooks(5);
        model.addAttribute("books", randomBooks);


        // Zwracamy widok home.html (bez /)
        return "home";
    }
}
