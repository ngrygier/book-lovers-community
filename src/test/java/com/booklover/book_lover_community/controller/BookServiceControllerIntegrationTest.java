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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;
    @Mock
    private UserService userService;
    @Mock
    private LibraryService libraryService;
    @Mock
    private BookStatsService bookStatsService;

    @InjectMocks
    private BookController bookController;

    @Mock
    private Model model;
    @Mock
    private HttpServletRequest request;
    @Mock
    private RedirectAttributes redirectAttributes;

    private User user;
    private Book book;
    private Library library;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        book = new Book();
        book.setId(100L);
        book.setReviews(new HashSet<>());

        library = new Library();
        library.setId(10L);
        library.setUser(user);
        library.setName("READ");
        library.getBooks().add(book);
    }

    // =======================
    // addCsrfToken
    // =======================
    @Test
    void addCsrfToken_withToken() {
        var token = mock(org.springframework.security.web.csrf.CsrfToken.class);
        when(request.getAttribute("_csrf")).thenReturn(token);
        bookController.addCsrfToken(model, request);
        verify(model).addAttribute("_csrf", token);
    }

    @Test
    void addCsrfToken_withoutToken() {
        when(request.getAttribute("_csrf")).thenReturn(null);
        bookController.addCsrfToken(model, request);
        verify(model, never()).addAttribute(eq("_csrf"), any());
    }

    // =======================
    // listBooks
    // =======================
    @Test
    void listBooks_success() {
        List<Book> books = List.of(book);
        when(bookService.getAllBooks()).thenReturn(books);

        String view = bookController.listBooks(model);

        verify(model).addAttribute("books", books);
        assertEquals("books", view);
    }

    // =======================
    // viewBook
    // =======================
    @Test
    void viewBook_success() {
        List<Library> libraries = List.of(library);
        BookStatsDto statsDto = new BookStatsDto(1L, 5.0, Map.of(5, 1L));

        when(bookService.getBookById(100L)).thenReturn(book);
        when(userService.getCurrentUser()).thenReturn(user);
        when(libraryService.getLibrariesByUser(user)).thenReturn(libraries);
        when(bookStatsService.getBookStats(100L)).thenReturn(statsDto);

        String view = bookController.viewBook(100L, model);

        verify(model).addAttribute("book", book);
        verify(model).addAttribute("stats", statsDto);
        assertEquals("book-details", view);
    }

    // =======================
    // addToLibrary
    // =======================
    @Test
    void addToLibrary_success() {
        when(bookService.getBookById(100L)).thenReturn(book);
        when(libraryService.getLibraryById(10L)).thenReturn(library);

        String view = bookController.addToLibrary(100L, 10L);
        assertEquals("redirect:/books/100", view);
        verify(libraryService).addBookToLibrary(library, book);
    }

    // =======================
    // addReview
    // =======================
    @Test
    void addReview_success() {
        ReviewDto dto = new ReviewDto();
        dto.setStars(5);
        dto.setContent("Great");

        when(bookService.getBookById(100L)).thenReturn(book);
        when(userService.getCurrentUser()).thenReturn(user);

        String view = bookController.addReview(100L, dto);
        assertEquals("redirect:/books/100", view);
        verify(bookService).addReview(book, user, dto);
    }

    // =======================
    // addToCustomLibrary
    // =======================
    @Test
    void addToCustomLibrary_success() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookService.findById(100L)).thenReturn(Optional.of(book));

        String view = bookController.addToCustomLibrary(100L, 10L);
        assertEquals("redirect:/books/100", view);
        verify(libraryService).addBookToCustomLibrary(user, 10L, book);
    }

    @Test
    void addToCustomLibrary_bookNotExist() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookService.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> bookController.addToCustomLibrary(100L, 10L));
    }

    // =======================
    // removeBookFromLibrary
    // =======================
    @Test
    void removeBookFromLibrary_success() {
        when(userService.getCurrentUser()).thenReturn(user);

        String view = bookController.removeBookFromLibrary(10L, 100L, redirectAttributes);
        assertEquals("redirect:/profile", view);
        verify(libraryService).removeBookFromLibrary(user, 10L, 100L);
    }

    @Test
    void removeBookFromLibrary_withException() {
        when(userService.getCurrentUser()).thenReturn(user);
        doThrow(new RuntimeException("error")).when(libraryService)
                .removeBookFromLibrary(user, 10L, 100L);

        String view = bookController.removeBookFromLibrary(10L, 100L, redirectAttributes);
        assertEquals("redirect:/profile", view);
        verify(redirectAttributes).addFlashAttribute("error", "error");
    }
}