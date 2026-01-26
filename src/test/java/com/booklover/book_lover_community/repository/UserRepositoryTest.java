package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstname("Jan")
                .lastname("Kowalski")
                .username("janek")
                .email("jan@mail.com")
                .password("password")
                .enabled(true)
                .accountLocked(false)
                .build();

        user = userRepository.save(user);
    }

    @Test
    void shouldFindUserByEmail() {
        // when
        Optional<User> result = userRepository.findByEmail("jan@mail.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("janek");
    }

    @Test
    void shouldFindUserByUsername() {
        // when
        Optional<User> result = userRepository.findByUsername("janek");

        // then
        assertThat(result).isPresent();
    }

    @Test
    void shouldCheckIfUserExistsByEmailAndUsername() {
        // then
        assertThat(userRepository.existsByEmail("jan@mail.com")).isTrue();
        assertThat(userRepository.existsByUsername("janek")).isTrue();
    }
}
