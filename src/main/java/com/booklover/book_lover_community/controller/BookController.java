package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.Dto.ReviewDto;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.model.Review;
import com.booklover.book_lover_community.service.BookService;
import com.booklover.book_lover_community.service.LibraryService;
import com.booklover.book_lover_community.service.UserService;
import com.booklover.book_lover_community.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final UserService userService;
    private final LibraryService libraryService;

    public BookController(BookService service, UserService userService, LibraryService libraryService) {
        this.bookService = service;
        this.userService = userService;
        this.libraryService = libraryService;
    }

    // ========================
    // Wyświetlanie książek
    // ========================
    @GetMapping
    public String listBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "books"; // szablon books.html
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        User currentUser = userService.getCurrentUser();

        List<Library> libraries = libraryService.getLibrariesByUser(currentUser);

        List<Book> readBooks = libraries != null && !libraries.isEmpty() ?
                libraries.stream()
                        .filter(lib -> "READ".equalsIgnoreCase(lib.getName()))
                        .flatMap(lib -> lib.getBooks().stream())
                        .toList() :
                List.of();

        model.addAttribute("book", book);
        model.addAttribute("libraries", libraries);
        model.addAttribute("reviews", book.getReviews());
        model.addAttribute("readBooks", readBooks);

        return "book-details";
    }

    // ========================
    // Dodawanie książki do biblioteki
    // ========================
    @PostMapping("/{id}/add-to-library")
    public String addToLibrary(@PathVariable Long id,
                               @RequestParam Long libraryId) {
        Book book = bookService.getBookById(id);
        libraryService.addBookToLibrary(libraryService.getLibraryById(libraryId), book);
        return "redirect:/books/" + id;
    }

    // ========================
    // Dodawanie recenzji (z DTO)
    // ========================
    @PostMapping("/{id}/add-review")
    @Transactional
    public String addReview(@PathVariable Long id,
                            @ModelAttribute @Valid ReviewDto reviewDto) {

        Book book = bookService.getBookById(id);
        User user = userService.getCurrentUser();

        // Inicjalizacja reviews, jeśli null
        if (book.getReviews() == null) {
            book.setReviews(new HashSet<>());
        }

        // Wywołanie serwisu do dodania recenzji
        bookService.addReview(book, user, reviewDto);

        return "redirect:/books/" + id;
    }

}
