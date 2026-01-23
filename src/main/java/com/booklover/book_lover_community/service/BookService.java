package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book createBook(Book book) {

        if (bookRepository.existsByTitle(book.getTitle())) {
            throw new IllegalArgumentException("Book with this title already exists");
        }

        return bookRepository.save(book);
    }

    @Transactional
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book not found"));
    }

    @Transactional
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    @Transactional
    public List<Book> searchByAuthor(String authorName) {
        return bookRepository
                .findByAuthor_FullName(authorName);
    }

}
