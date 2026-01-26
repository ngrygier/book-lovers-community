package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.repository.AuthorRepository;
import com.booklover.book_lover_community.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Author testAuthor;

    @BeforeEach
    void setup() {

        bookRepository.deleteAll();
        authorRepository.deleteAll();

        testAuthor = new Author();
        testAuthor.setFullName("Test Author");
        authorRepository.save(testAuthor);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminPageAccessible() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddBook() throws Exception {
        mockMvc.perform(post("/admin/books/add")
                        .param("title", "Integration Test Book")
                        .param("author.id", testAuthor.getId().toString())
                        .with(csrf()) // CSRF konieczne przy POST
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books/index"));

        // Weryfikacja w bazie
        assertEquals(1, bookRepository.count());
        Book savedBook = bookRepository.findAll().get(0);
        assertEquals("Integration Test Book", savedBook.getTitle());
        assertEquals(testAuthor.getId(), savedBook.getAuthor().getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testBooksList() throws Exception {
        Book book = new Book();
        book.setTitle("Existing Book");
        book.setAuthor(testAuthor);
        bookRepository.save(book);

        mockMvc.perform(get("/admin/books/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/books/index"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteBook() throws Exception {
        Book book = new Book();
        book.setTitle("Book to Delete");
        book.setAuthor(testAuthor);
        bookRepository.save(book);

        Long bookId = bookRepository.findAll().get(0).getId();

        mockMvc.perform(post("/admin/books/delete/" + bookId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books/index"));

        assertEquals(0, bookRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowAddBookForm() throws Exception {
        mockMvc.perform(get("/admin/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/books/add"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"));
    }
}
