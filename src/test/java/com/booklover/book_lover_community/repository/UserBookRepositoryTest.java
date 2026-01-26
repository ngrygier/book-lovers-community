package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.UserBook;
import com.booklover.book_lover_community.user.ShelfStatus;
import com.booklover.book_lover_community.user.User;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class UserBookRepositoryTest {

    @Autowired
    private UserBookRepository userBookRepository;

    @Autowired
    private UserRepository userRepository;
    private Book book;
    private User user;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstname("Anna")
                .lastname("Nowak")
                .username("anna")
                .email("anna@mail.com")
                .password("password")
                .enabled(true)
                .accountLocked(false)
                .build();

        user = userRepository.save(user);

        Author author = new Author();
        author.setFullName("iga swiatek");
        authorRepository.save(author);

        book = new Book();
        book.setAuthor(author);
        book.setTitle("ao");
        bookRepository.save(book);
    }

    @Test
    void shouldFindAllUserBooksByUser() {
        // given
        UserBook userBook = new UserBook();
        userBook.setUser(user);
        userBook.setBook(book);
        userBook.setStatus(ShelfStatus.READ);
        userBookRepository.save(userBook);

        // when
        List<UserBook> result = userBookRepository.findAllByUser(user);

        // then
        assertThat(result).hasSize(1);
    }
}

