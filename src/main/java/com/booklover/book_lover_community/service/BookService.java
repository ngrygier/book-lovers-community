package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.model.Author;
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


    //utworzenie książki
    @Transactional
    public Book createBook(Book book) {

        if (bookRepository.existsByTitle(book.getTitle())) {
            throw new IllegalArgumentException("Book with this title already exists");
        }

        return bookRepository.save(book);
    }


    //pozyskanie przez id
    @Transactional
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book not found"));
    }


    //zwraca wszystkie książki
    @Transactional
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    //usuwa książkę
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    //szuka po autorze
    @Transactional
    public List<Book> searchByAuthor(Author author) {
        return bookRepository
                .findByAuthor_FullName(author.getFullName());
    }

    //dodaje książkę
    public Book save(Book book){
        return bookRepository.save(book);
    }

    //szukanie wszytskich
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    //usuwanie książki
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }


}
