package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.Dto.EditProfileDto;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.repository.LibraryRepository;
import com.booklover.book_lover_community.repository.ReviewRepository;
import com.booklover.book_lover_community.repository.UserRepository;
import com.booklover.book_lover_community.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class ProfileControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("testuser@example.com");
        testUser.setEnabled(true);
        testUser.setLastname("makowka");
        testUser.setFirstname("jadzia");
        userRepository.save(testUser);
    }

    @Test
    void shouldShowProfilePage() throws Exception {
        mockMvc.perform(get("/profile")
                        .with(user(testUser.getUsername()).password("password").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("editProfile"))
                .andExpect(model().attributeExists("defaultLibraries"))
                .andExpect(model().attributeExists("customLibraries"));
    }

    @Test
    void shouldEditProfileSuccessfully() throws Exception {
        mockMvc.perform(post("/profile/edit")
                        .with(user(testUser.getUsername()).password("password").roles("USER"))
                        .with(csrf())
                        .param("username", "updatedUser")
                        .param("email", "updated@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        // Sprawdzenie w bazie
        User updated = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updated.getUsername()).isEqualTo("updatedUser");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void shouldAddCustomLibrary() throws Exception {
        mockMvc.perform(post("/profile/libraries/add")
                        .with(user(testUser.getUsername()).password("password").roles("USER"))
                        .with(csrf())
                        .param("name", "MyLibrary"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        // Weryfikacja w bazie
        List<Library> libraries = libraryRepository.findByUser(testUser);
        assertThat(libraries).extracting(Library::getName).contains("MyLibrary");
    }

    @Test
    void shouldNotAddEmptyLibrary() throws Exception {
        mockMvc.perform(post("/profile/libraries/add")
                        .with(user(testUser.getUsername()).password("password").roles("USER"))
                        .with(csrf())
                        .param("name", " "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        List<Library> libraries = libraryRepository.findByUser(testUser);
        assertThat(libraries).isEmpty();
    }

    @Test
    void shouldNotAddDuplicateLibrary() throws Exception {
        // dodajemy pierwszą
        Library lib = new Library();
        lib.setName("MyLibrary");
        lib.setUser(testUser);
        libraryRepository.save(lib);

        // próba dodania tej samej nazwy
        mockMvc.perform(post("/profile/libraries/add")
                        .with(user(testUser.getUsername()).password("password").roles("USER"))
                        .with(csrf())
                        .param("name", "MyLibrary"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        List<Library> libraries = libraryRepository.findByUser(testUser);
        assertThat(libraries).hasSize(1); // nadal tylko jedna
    }
}

