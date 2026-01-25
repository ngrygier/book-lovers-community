package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.Dto.ReviewDto;
import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Review;
import com.booklover.book_lover_community.repository.BookRepository;
import com.booklover.book_lover_community.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ========================
    // PODSTAWOWE METODY CRUD
    // ========================

    // Tworzenie książki (sprawdza czy już istnieje)
    @Transactional
    public Book createBook(Book book) {
        if (bookRepository.existsByTitle(book.getTitle())) {
            throw new IllegalArgumentException("Book with this title already exists");
        }
        return bookRepository.save(book);
    }

    // Pobranie książki po ID
    @Transactional
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    // Pobranie wszystkich książek
    @Transactional
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Usunięcie książki po ID (sprawdza czy istnieje)
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    // Zapis lub aktualizacja książki
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    // Usuwanie książki po ID (bez sprawdzania)
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    // ========================
    // METODY SPECJALNE
    // ========================

    // Pobranie losowych książek (np. do rekomendacji na stronie głównej)
    @Transactional
    public List<Book> getRandomBooks(int count) {
        List<Book> allBooks = bookRepository.findAllBooks();
        Collections.shuffle(allBooks);
        if (count > allBooks.size()) {
            count = allBooks.size();
        }
        return allBooks.subList(0, count);
    }

    // Wyszukiwanie książek po tytule lub autorze (zabezpieczenie na puste query)
    @Transactional
    public List<Book> searchBooks(String query, String type) {
        if (query == null || query.isBlank()) {
            return getRandomBooks(5); // domyślnie losowe książki
        }

        if ("title".equalsIgnoreCase(type)) {
            return bookRepository.findByTitleContainingIgnoreCase(query);
        } else if ("author".equalsIgnoreCase(type)) {
            return bookRepository.findByAuthorFullNameContainingIgnoreCase(query);
        }

        return getRandomBooks(5); // domyślnie losowe książki
    }

    // Szukanie książek po autorze (stara metoda)
    @Transactional
    public List<Book> searchByAuthor(Author author) {
        return bookRepository.findByAuthor_FullName(author.getFullName());
    }

    // Pobranie wszystkich książek (stara metoda)
    public List<Book> findAll() {
        return bookRepository.findAll();

    }

    @Transactional
    public void addReview(Book book, User user, ReviewDto reviewDto) {

        
        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setStars(reviewDto.getStars());
        review.setContent(reviewDto.getContent());

        if (book.getReviews() == null) {
            book.setReviews(new HashSet<>());
        }
        book.getReviews().add(review);

        double total = book.getReviews().stream().mapToInt(Review::getStars).sum();
        book.setRating(total / book.getReviews().size());
        book.setRatingCount(book.getReviews().size());

        bookRepository.save(book);
    }



}
