package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.Dto.BookStatsDto;
import com.booklover.book_lover_community.Dto.ReviewDto;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.model.Review;
import com.booklover.book_lover_community.service.BookService;
import com.booklover.book_lover_community.service.BookStatsService;
import com.booklover.book_lover_community.service.LibraryService;
import com.booklover.book_lover_community.service.UserService;
import com.booklover.book_lover_community.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final UserService userService;
    private final LibraryService libraryService;
    private final BookStatsService bookStatsService;

    public BookController(BookService service, UserService userService, LibraryService libraryService, BookStatsService bookStatsService) {
        this.bookService = service;
        this.userService = userService;
        this.libraryService = libraryService;
        this.bookStatsService = bookStatsService;
    }
    @ModelAttribute
    public void addCsrfToken(Model model, HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
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

        // Wszystkie biblioteki użytkownika
        List<Library> libraries = libraryService.getLibrariesByUser(currentUser);

        // Lista książek w bibliotece "READ"
        List<Book> readBooks = libraries != null && !libraries.isEmpty() ?
                libraries.stream()
                        .filter(lib -> "READ".equalsIgnoreCase(lib.getName()))
                        .flatMap(lib -> lib.getBooks().stream())
                        .toList() :
                List.of();

        // Biblioteki niestandardowe (inne niż TO_READ, READING, READ)
        List<Library> customLibraries = libraries != null ?
                libraries.stream()
                        .filter(lib -> !List.of("TO_READ", "READING", "READ").contains(lib.getName().toUpperCase()))
                        .toList() :
                List.of();

        // Statystyki książki
        BookStatsDto stats = bookStatsService.getBookStats(id);

        // Dodanie atrybutów do modelu
        model.addAttribute("book", book);
        model.addAttribute("stats", stats);
        model.addAttribute("libraries", libraries);
        model.addAttribute("customLibraries", customLibraries);
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


    @PostMapping("/{bookId}/add-to-custom-library")
    public String addToCustomLibrary(@PathVariable Long bookId, Long customLibraryId) {

        User user = userService.getCurrentUser();

        // Pobranie książki
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Książka nie istnieje"));

        // Dodanie książki do custom library
        libraryService.addBookToCustomLibrary(user, customLibraryId, book);

        // Po dodaniu wracamy do widoku książki
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/libraries/{libraryId}/remove-book/{bookId}")
    public String removeBookFromLibrary(@PathVariable Long libraryId,
                                        @PathVariable Long bookId,
                                        RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        try {
            libraryService.removeBookFromLibrary(currentUser, libraryId, bookId);
            redirectAttributes.addFlashAttribute("message", "Book removed successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }





}
