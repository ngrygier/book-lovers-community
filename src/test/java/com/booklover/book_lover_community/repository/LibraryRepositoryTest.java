package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LibraryRepositoryTest {

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired AuthorRepository authorRepository;

    @Test
    void shouldFindLibrariesByUserId() {
        // given
        User user = new User();
        user.setUsername("user1");
        user.setFirstname("jadzia");
        user.setLastname("makowka");
        user.setEmail("jadzia@com");
        user.setPassword("haslo");
        user = userRepository.save(user);

        Library library = new Library();
        library.setName("Moja biblioteka");
        library.setUser(user);
        libraryRepository.save(library);

        // when
        List<Library> libraries = libraryRepository.findByUserId(user.getId().longValue());

        // then
        assertThat(libraries).hasSize(1);
    }

    @Test
    void shouldCheckIfLibraryExistsIgnoringCase() {
        // given
        User user = new User();
        user.setUsername("user2");
        user.setFirstname("jadzia");
        user.setLastname("makowka");
        user.setEmail("jadzia@com");
        user.setPassword("haslo");
        user = userRepository.save(user);

        Library library = new Library();
        library.setName("Fantasy");
        library.setUser(user);
        libraryRepository.save(library);

        // when
        boolean exists = libraryRepository.existsByUserIdAndNameIgnoreCase(
                user.getId(),
                "fantasy"
        );

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCountReadersForBook() {
        // given

        Author author = new Author();
        author.setFullName("iga swiatek");
        author = authorRepository.save(author);

        User user = new User();
        user.setUsername("user3");
        user.setFirstname("jadzia");
        user.setLastname("makowka");
        user.setEmail("jadzia@com");
        user.setPassword("haslo");
        user = userRepository.save(user);

        Book book = new Book();
        book.setTitle("Hobbit");
        book.setAuthor(author);
        book = bookRepository.save(book);

        Library library = new Library();
        library.setUser(user);
        library.getBooks().add(book);
        library.setName("makowka");
        libraryRepository.save(library);

        // when
        Long readers = libraryRepository.countReaders(book.getId());

        // then
        assertThat(readers).isEqualTo(1L);
    }
}
