package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.repository.AuthorRepository;
import com.booklover.book_lover_community.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setFullName("Test Author");
        authorRepository.saveAndFlush(author);
    }

    /* =========================
       PANEL ADMINA
     ========================= */

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRenderAdminPanel() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    /* =========================
       KSIĄŻKI
     ========================= */

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAddBookForm() throws Exception {
        mockMvc.perform(get("/admin/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/books/add"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPopulateAddBookFormModel() throws Exception {
        mockMvc.perform(get("/admin/books/add"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRedirectAfterAddingBook() throws Exception {
        mockMvc.perform(post("/admin/books/add")
                        .param("title", "New Book")
                        .param("author.id", author.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books/index"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPersistBookInDatabase() throws Exception {
        long before = bookRepository.count();

        mockMvc.perform(post("/admin/books/add")
                .param("title", "Persisted Book")
                .param("author.id", author.getId().toString())
                .with(csrf()));

        assertThat(bookRepository.count()).isEqualTo(before + 1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowBooksList() throws Exception {
        mockMvc.perform(get("/admin/books/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/books/index"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteBook() throws Exception {
        Book book = new Book();
        book.setTitle("To Delete");
        book.setAuthor(author);
        bookRepository.saveAndFlush(book);

        long before = bookRepository.count();

        mockMvc.perform(post("/admin/books/delete/{id}", book.getId())
                .with(csrf()));

        assertThat(bookRepository.count()).isEqualTo(before - 1);
    }

    /* =========================
       AUTORZY
     ========================= */

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAddAuthorForm() throws Exception {
        mockMvc.perform(get("/admin/authors/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/authors/add"))
                .andExpect(model().attributeExists("author"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRedirectAfterAddingAuthor() throws Exception {
        mockMvc.perform(post("/admin/authors/add")
                        .param("fullName", "New Author")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/authors"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAuthorsList() throws Exception {
        mockMvc.perform(get("/admin/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/authors/index"))
                .andExpect(model().attributeExists("authors"));
    }

    /* =========================
       BEZPIECZEŃSTWO
     ========================= */

    @Test
    void shouldDenyAccessForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection());
    }
}
