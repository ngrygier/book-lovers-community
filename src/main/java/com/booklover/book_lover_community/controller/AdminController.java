package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.repository.BookRepository;
import com.booklover.book_lover_community.repository.ReviewRepository;
import com.booklover.book_lover_community.repository.UserRepository;
import com.booklover.book_lover_community.service.AuthorService;
import com.booklover.book_lover_community.service.BookService;
import com.booklover.book_lover_community.service.LibraryService;
import com.booklover.book_lover_community.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final LibraryService libraryService;
    private final BookRepository bookRepository;

   //   PANEL ADMINA

    @GetMapping
    public String admin() {
        return "admin";
    }

    //  KSIAZKI

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        return "admin/books/add";
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.save(book);
        return "redirect:/admin/books/index";
    }

    @GetMapping("/books/index")
    public String booksList(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "admin/books/index";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBookCompletely(id);
        return "redirect:/admin/books/index";
    }


    //  AUTORZY

    @GetMapping("/authors/add")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/authors/add";
    }

    @PostMapping("/authors/add")
    public String addAuthor(@ModelAttribute Author author) {
        authorService.save(author);
        return "redirect:/admin/authors";
    }

    @GetMapping("/authors")
    public String authors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "admin/authors/index";
    }

    @PostMapping("/authors/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteById(id);
        return "redirect:/admin/authors";
    }

    //  UZYTKOWNICY

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String userDetails(@PathVariable Long id, Model model) {
        User user = userRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("reviews", reviewRepository.findByUserId(id));
        return "admin/user-details";
    }

    @Transactional
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/libraries/delete/{id}")
    public String deleteLibrary(@PathVariable Long id) {
        libraryService.deleteLibrary(id);
        return "redirect:/admin/users";
    }

    //  RECENZJE

    @PostMapping("/reviews/delete/{reviewId}")
    public String deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId
    ) {
        reviewRepository.deleteById(reviewId);
        return "redirect:/admin/users/" + userId;
    }
}
