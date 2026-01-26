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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        Author author = new Author();
        author.setFullName("George Orwell");
        author = authorRepository.save(author);

        Book book1 = new Book();
        book1.setTitle("1984");
        book1.setAuthor(author);


        Book book2 = new Book();
        book2.setTitle("Animal Farm");
        book2.setAuthor(author);

        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldRedirectAdminToAdminPage() throws Exception {

        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/admin")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturnHomeViewForUser() throws Exception {

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("type", "title"));
    }


    @Test
    void shouldReturnHomeViewWhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("books"));
    }


    @Test
    void shouldSearchBooksByTitle() throws Exception {

        mockMvc.perform(get("/home")
                        .param("query", "1984")
                        .param("type", "title"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("query", "1984"))
                .andExpect(model().attribute("type", "title"));

        assertThat(bookRepository.findByTitleContainingIgnoreCase("1984")).hasSize(1);
    }


    @Test
    void shouldSearchBooksByAuthor() throws Exception {

        mockMvc.perform(get("/home")
                        .param("query", "Orwell")
                        .param("type", "author"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("type", "author"));

        assertThat(bookRepository.findByAuthorFullNameContainingIgnoreCase("Orwell")).hasSize(2);
    }


    @Test
    void shouldReturnRandomBooksWhenQueryIsEmpty() throws Exception {

        mockMvc.perform(get("/home")
                        .param("query", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("books"));
    }
}
