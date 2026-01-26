package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Review;
import com.booklover.book_lover_community.model.UserBook;
import com.booklover.book_lover_community.repository.*;
import com.booklover.book_lover_community.user.ShelfStatus;
import com.booklover.book_lover_community.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserDataControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private UserBookRepository userBookRepository;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.pl");
        user.setPassword("password");
        user.setEnabled(true);
        user.setFirstname("jadzia");
        user.setLastname("makowka");
        user = userRepository.save(user);

        Author author = new Author();
        author.setFullName("George Orwell");
        author = authorRepository.save(author);
        author = authorRepository.findById(author.getId()).orElseThrow();

        book = new Book();
        book.setTitle("1984");
        book.setAuthor(author);
        book = bookRepository.save(book);

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setStars(5);
        review.setContent("Great book");
        reviewRepository.save(review);

        UserBook userBook = new UserBook();
        userBook.setBook(book);
        userBook.setUser(user);
        userBook.setStatus(ShelfStatus.READ);
        userBook.setAddedDate(LocalDateTime.now());
        userBookRepository.save(userBook);
    }


    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void shouldExportUserDataAsJson() throws Exception {

        mockMvc.perform(get("/user/data/export/json/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.books[0].title").value("1984"))
                .andExpect(jsonPath("$.reviews[0].content").value("Great book"));
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldExportUserDataAsCsv() throws Exception {
        mockMvc.perform(get("/user/data/export/csv/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"user_data.csv\""))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Book Title,Author,Review,Rating")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1984")));
    }
}

