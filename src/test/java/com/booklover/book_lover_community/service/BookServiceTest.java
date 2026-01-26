package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.Dto.ReviewDto;
import com.booklover.book_lover_community.model.*;
import com.booklover.book_lover_community.repository.BookRepository;
import com.booklover.book_lover_community.repository.LibraryRepository;
import com.booklover.book_lover_community.repository.UserBookRepository;
import com.booklover.book_lover_community.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserBookRepository userBookRepository;

    @Mock
    private LibraryRepository libraryRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private User user;
    private Author author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        author = new Author();
        author.setFullName("Test Author");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor(author);

        user = new User();
        user.setId(1);
        user.setUsername("testuser");
    }

    @Test
    void createBook_shouldSaveBook_whenTitleDoesNotExist() {
        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(false);
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.createBook(book);

        assertEquals(book, result);
        verify(bookRepository).save(book);
    }

    @Test
    void createBook_shouldThrowException_whenTitleExists() {
        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(book));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void getBookById_shouldReturnBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertEquals(book, result);
    }

    @Test
    void getBookById_shouldThrowException_whenNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(1L));
    }

    @Test
    void getAllBooks_shouldReturnList() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookService.getAllBooks();

        assertEquals(1, result.size());
    }

    @Test
    void deleteBook_shouldDelete_whenExists() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_shouldThrowException_whenNotExists() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteBook(1L));
    }

    @Test
    void deleteBookCompletely_shouldRemoveFromLibrariesAndDelete() {
        Library library = new Library();
        library.setBooks(new HashSet<>(Set.of(book)));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(libraryRepository.findAllByBooksContains(book))
                .thenReturn(List.of(library));

        bookService.deleteBookCompletely(1L);

        assertFalse(library.getBooks().contains(book));
        verify(bookRepository).delete(book);
        verify(libraryRepository).saveAll(any());
        verify(libraryRepository).flush();
    }

    @Test
    void deleteBookCompletely_shouldThrowException_whenBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteBookCompletely(1L));
    }

    @Test
    void save_shouldPersistBook() {
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.save(book);

        assertEquals(book, result);
    }

    @Test
    void deleteById_shouldInvokeRepository() {
        bookService.deleteById(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void getRandomBooks_shouldReturnLimitedList() {
        when(bookRepository.findAllBooks()).thenReturn(List.of(book));

        List<Book> result = bookService.getRandomBooks(5);

        assertEquals(1, result.size());
    }

    @Test
    void searchBooks_shouldSearchByTitle() {
        when(bookRepository.findByTitleContainingIgnoreCase("Test"))
                .thenReturn(List.of(book));

        List<Book> result = bookService.searchBooks("Test", "title");

        assertEquals(1, result.size());
    }

    @Test
    void searchBooks_shouldSearchByAuthor() {
        when(bookRepository.findByAuthorFullNameContainingIgnoreCase("Author"))
                .thenReturn(List.of(book));

        List<Book> result = bookService.searchBooks("Author", "author");

        assertEquals(1, result.size());
    }

    @Test
    void searchBooks_shouldReturnRandom_whenQueryBlank() {
        when(bookRepository.findAllBooks()).thenReturn(List.of(book));

        List<Book> result = bookService.searchBooks("", "title");

        assertEquals(1, result.size());
    }

    @Test
    void searchBooks_shouldReturnRandom_whenTypeUnknown() {
        when(bookRepository.findAllBooks()).thenReturn(List.of(book));

        List<Book> result = bookService.searchBooks("abc", "unknown");

        assertEquals(1, result.size());
    }

    @Test
    void searchByAuthor_shouldReturnBooks() {
        when(bookRepository.findByAuthor_FullName(author.getFullName()))
                .thenReturn(List.of(book));

        List<Book> result = bookService.searchByAuthor(author);

        assertEquals(1, result.size());
    }

    @Test
    void findById_shouldReturnOptional() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void addReview_shouldAddReviewAndUpdateRating() {
        ReviewDto dto = new ReviewDto();
        dto.setStars(4);
        dto.setContent("Good");

        book.setReviews(new HashSet<>());
        when(bookRepository.save(book)).thenReturn(book);

        bookService.addReview(book, user, dto);

        assertEquals(1, book.getReviews().size());
        assertEquals(4.0, book.getRating());
        assertEquals(1, book.getRatingCount());
    }

    @Test
    void getBooksByUser_shouldReturnBooks() {
        UserBook userBook = new UserBook();
        userBook.setBook(book);

        when(userBookRepository.findAllByUser(user))
                .thenReturn(List.of(userBook));

        List<Book> result = bookService.getBooksByUser(user);

        assertEquals(1, result.size());
    }
}
