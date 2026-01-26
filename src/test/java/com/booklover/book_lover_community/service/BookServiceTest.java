package com.booklover.book_lover_community.service;
import com.booklover.book_lover_community.Dto.ReviewDto;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.UserBook;
import com.booklover.book_lover_community.repository.BookRepository;
import com.booklover.book_lover_community.repository.UserBookRepository;
import com.booklover.book_lover_community.user.User;
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

    @InjectMocks
    private BookService bookService;

    private Book book;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        user = new User();
        user.setId(1);
        user.setUsername("testuser");
    }

    @Test
    void createBook_shouldSaveBook_whenTitleDoesNotExist() {
        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(false);
        when(bookRepository.save(book)).thenReturn(book);

        Book created = bookService.createBook(book);

        assertNotNull(created);
        assertEquals("Test Book", created.getTitle());
        verify(bookRepository, times(1)).existsByTitle(book.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void createBook_shouldThrowException_whenTitleExists() {
        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.createBook(book)
        );

        assertEquals("Book with this title already exists", exception.getMessage());
        verify(bookRepository, times(1)).existsByTitle(book.getTitle());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void getBookById_shouldReturnBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book found = bookService.getBookById(1L);

        assertNotNull(found);
        assertEquals("Test Book", found.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void addReview_shouldAddReviewAndUpdateRating() {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setStars(5);
        reviewDto.setContent("Great book!");

        book.setReviews(new HashSet<>());

        when(bookRepository.save(book)).thenReturn(book);

        bookService.addReview(book, user, reviewDto);

        assertEquals(1, book.getReviews().size());
        assertEquals(5.0, book.getRating());
        assertEquals(1, book.getRatingCount());

        // Sprawdzenie, że metoda save została wywołana
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void getBooksByUser_shouldReturnBooksForUser() {
        UserBook userBook = new UserBook();
        userBook.setBook(book);
        when(userBookRepository.findAllByUser(user)).thenReturn(Collections.singletonList(userBook));

        List<Book> books = bookService.getBooksByUser(user);

        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());
        verify(userBookRepository, times(1)).findAllByUser(user);
    }
}
