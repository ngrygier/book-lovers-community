package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.repository.AuthorRepository;
import com.booklover.book_lover_community.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Author authorRowling;
    private Author authorTolkien;

    @BeforeEach
    void setUp() {

        authorRowling = new Author();
        authorRowling.setFullName("J.K. Rowling");
        authorRepository.save(authorRowling);

        authorTolkien = new Author();
        authorTolkien.setFullName("J.R.R. Tolkien");
        authorRepository.save(authorTolkien);

        Book book1 = new Book();
        book1.setTitle("Harry Potter");
        book1.setAuthor(authorRowling);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("The Hobbit");
        book2.setAuthor(authorTolkien);
        bookRepository.save(book2);
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    void home_adminUser_shouldRedirectToAdmin() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }


    @Test
    @WithMockUser(roles = "USER")
    void home_user_shouldReturnHomeView() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", hasSize(greaterThanOrEqualTo(1))));
    }


    @Test
    void home_anonymousUser_shouldReturnHomeView() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("books"));
    }


    @Test
    void home_searchByTitle_shouldReturnMatchingBook() throws Exception {
        mockMvc.perform(get("/home")
                        .param("query", "Harry")
                        .param("type", "title"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("books", hasSize(1)))
                .andExpect(model().attribute("books",
                        hasItem(hasProperty("title", is("Harry Potter")))
                ));
    }


    @Test
    void home_searchByAuthor_shouldReturnMatchingBook() throws Exception {
        mockMvc.perform(get("/home")
                        .param("query", "Tolkien")
                        .param("type", "author"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("books", hasSize(1)))
                .andExpect(model().attribute("books",
                        hasItem(hasProperty("title", is("The Hobbit")))
                ));
    }


    @Test
    void home_noQuery_shouldReturnRandomBooks() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("books", not(empty())));
    }
}
