package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setFullName("Frank Herbert");
        authorRepository.save(author);

        Book book1 = new Book();
        book1.setTitle("Dune");
        book1.setAuthor(author);

        Book book2 = new Book();
        book2.setTitle("Dune Messiah");
        book2.setAuthor(author);

        bookRepository.saveAll(List.of(book1, book2));
    }

    @Test
    void shouldCheckIfBookExistsByTitle() {
        // when
        boolean exists = bookRepository.existsByTitle("Dune");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindBooksByExactAuthorName() {
        // when
        List<Book> books = bookRepository.findByAuthor_FullName("Frank Herbert");

        // then
        assertThat(books).hasSize(2);
    }

    @Test
    void shouldFindBooksByTitleContainingIgnoreCase() {
        // when
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase("dune");

        // then
        assertThat(books).hasSize(2);
    }

    @Test
    void shouldFindBooksByAuthorNameContainingIgnoreCase() {
        // when
        List<Book> books = bookRepository.findByAuthorFullNameContainingIgnoreCase("herbert");

        // then
        assertThat(books).hasSize(2);
    }

    @Test
    void shouldReturnAllBooksUsingCustomQuery() {
        // when
        List<Book> books = bookRepository.findAllBooks();

        // then
        assertThat(books).hasSize(2);
    }
}
