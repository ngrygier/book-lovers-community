package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.model.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldSaveAndFindAuthorById() {
        // given
        Author author = new Author();
        author.setFullName("George Orwell");

        Author saved = authorRepository.save(author);

        // when
        Optional<Author> result = authorRepository.findById(saved.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("George Orwell");
    }

    @Test
    void shouldDeleteAuthorById() {
        // given
        Author author = new Author();
        author.setFullName("J.R.R. Tolkien");
        Author saved = authorRepository.save(author);

        // when
        authorRepository.deleteById(saved.getId());

        // then
        assertThat(authorRepository.findById(saved.getId())).isEmpty();
    }
}
