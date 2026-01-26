package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.repository.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryRepository libraryRepository;

    @InjectMocks
    private LibraryService libraryService;

    private User user;
    private Library library;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        library = new Library();
        library.setId(10L);
        library.setUser(user);
        library.setBooks(new HashSet<>());

        book = new Book();
        book.setId(100L);
    }

    // -------------------- GET LIBRARY BY ID --------------------
    @Test
    void getLibraryById_success() {
        when(libraryRepository.findById(10L)).thenReturn(Optional.of(library));
        assertEquals(library, libraryService.getLibraryById(10L));
    }

    @Test
    void getLibraryById_notFound() {
        when(libraryRepository.findById(20L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> libraryService.getLibraryById(20L));
    }

    // -------------------- GET LIBRARIES BY USER --------------------
    @Test
    void getLibrariesByUser_success() {
        List<Library> libs = List.of(library);
        when(libraryRepository.findByUserId(1L)).thenReturn(libs);
        assertEquals(libs, libraryService.getLibrariesByUser(user));
    }

    // -------------------- ADD BOOK TO LIBRARY --------------------
    @Test
    void addBookToLibrary_success() {
        when(libraryRepository.save(any(Library.class))).thenReturn(library);
        libraryService.addBookToLibrary(library, book);
        assertTrue(library.getBooks().contains(book));
    }

    // -------------------- GET LIBRARY BY USER AND NAME --------------------
    @Test
    void getLibraryByUserAndName_success() {
        when(libraryRepository.findByUserAndName(user, "MyLib")).thenReturn(Optional.of(library));
        assertEquals(library, libraryService.getLibraryByUserAndName(user, "MyLib"));
    }

    @Test
    void getLibraryByUserAndName_notFound() {
        when(libraryRepository.findByUserAndName(user, "Unknown")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> libraryService.getLibraryByUserAndName(user, "Unknown"));
    }

    // -------------------- ADD BOOK TO CUSTOM LIBRARY --------------------
    @Test
    void addBookToCustomLibrary_success() {
        when(libraryRepository.findById(10L)).thenReturn(Optional.of(library));
        when(libraryRepository.save(any(Library.class))).thenReturn(library);

        libraryService.addBookToCustomLibrary(user, 10L, book);
        assertTrue(library.getBooks().contains(book));
    }

    @Test
    void addBookToCustomLibrary_libraryNotExist() {
        when(libraryRepository.findById(20L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> libraryService.addBookToCustomLibrary(user, 20L, book));
    }

    @Test
    void addBookToCustomLibrary_wrongOwner() {
        User other = new User();
        other.setId(2);
        library.setUser(other);
        when(libraryRepository.findById(10L)).thenReturn(Optional.of(library));
        assertThrows(RuntimeException.class,
                () -> libraryService.addBookToCustomLibrary(user, 10L, book));
    }

    // -------------------- REMOVE BOOK FROM LIBRARY --------------------
    @Test
    void removeBookFromLibrary_success() {
        library.getBooks().add(book);
        when(libraryRepository.findById(10L)).thenReturn(Optional.of(library));
        when(libraryRepository.save(any(Library.class))).thenReturn(library);

        libraryService.removeBookFromLibrary(user, 10L, 100L);
        assertFalse(library.getBooks().contains(book));
        assertNull(book.getLibrary());
    }

    @Test
    void removeBookFromLibrary_libraryNotExist() {
        when(libraryRepository.findById(20L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> libraryService.removeBookFromLibrary(user, 20L, 100L));
    }

    @Test
    void removeBookFromLibrary_wrongOwner() {
        User other = new User();
        other.setId(2);
        library.setUser(other);
        when(libraryRepository.findById(10L)).thenReturn(Optional.of(library));
        assertThrows(RuntimeException.class,
                () -> libraryService.removeBookFromLibrary(user, 10L, 100L));
    }

    @Test
    void removeBookFromLibrary_bookNotExist() {
        when(libraryRepository.findById(10L)).thenReturn(Optional.of(library));
        assertThrows(RuntimeException.class,
                () -> libraryService.removeBookFromLibrary(user, 10L, 999L));
    }
}