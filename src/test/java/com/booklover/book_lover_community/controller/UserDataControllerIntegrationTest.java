package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.*;
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
        user.setFirstname("jadzia");
        user.setLastname("makowka");
        user.setEmail("test@test.pl");
        user.setPassword("password");
        user.setEnabled(true);
        user = userRepository.save(user);

        Author author = new Author();
        author.setFullName("George Orwell");
        author = authorRepository.save(author);

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
    @WithMockUser(roles = "USER")
    void exportJson_shouldReturnOkStatus() throws Exception {
        mockMvc.perform(get("/user/data/export/json/{id}", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void exportJson_shouldContainUserData() throws Exception {
        mockMvc.perform(get("/user/data/export/json/{id}", user.getId()))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void exportJson_shouldContainBooks() throws Exception {
        mockMvc.perform(get("/user/data/export/json/{id}", user.getId()))
                .andExpect(jsonPath("$.books[0].title").value("1984"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void exportJson_shouldContainReviews() throws Exception {
        mockMvc.perform(get("/user/data/export/json/{id}", user.getId()))
                .andExpect(jsonPath("$.reviews[0].content").value("Great book"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void exportCsv_shouldReturnOkStatus() throws Exception {
        mockMvc.perform(get("/user/data/export/csv/{id}", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void exportCsv_shouldHaveCsvHeaders() throws Exception {
        mockMvc.perform(get("/user/data/export/csv/{id}", user.getId()))
                .andExpect(header().string("Content-Type", "text/csv"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void exportCsv_shouldContainHeaderRow() throws Exception {
        mockMvc.perform(get("/user/data/export/csv/{id}", user.getId()))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Book Title,Author,Review,Rating")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void exportCsv_shouldContainBookTitle() throws Exception {
        mockMvc.perform(get("/user/data/export/csv/{id}", user.getId()))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1984")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void importJson_shouldReturnOkStatus() throws Exception {
        String json = """
                {
                  "books": [],
                  "reviews": []
                }
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes()
        );

        mockMvc.perform(multipart("/user/data/import/json/{id}", user.getId())
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "USER")
    void importJson_shouldPersistReviewWhenBookExists() throws Exception {
        String json = """
                {
                  "books": [],
                  "reviews": [
                    {
                      "bookId": %d,
                      "text": "Excellent",
                      "rating": 4
                    }
                  ]
                }
                """.formatted(book.getId());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes()
        );

        mockMvc.perform(multipart("/user/data/import/json/{id}", user.getId())
                .file(file)
                .with(csrf()));

        assertThat(reviewRepository.findAll())
                .anyMatch(r -> "Excellent".equals(r.getContent()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void importJson_shouldIgnoreReviewWithoutBookId() throws Exception {
        long countBefore = reviewRepository.count();

        String json = """
            {
              "books": [],
              "reviews": [
                { "text": "No book", "rating": 3 }
              ]
            }
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes()
        );

        mockMvc.perform(multipart("/user/data/import/json/{id}", user.getId())
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk());

        long countAfter = reviewRepository.count();

        assertThat(countAfter).isEqualTo(countBefore);
    }

}
