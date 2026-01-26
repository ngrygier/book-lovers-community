package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.repository.UserRepository;
import com.booklover.book_lover_community.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstname("jadzia")
                .lastname("makowka")
                .createdDate(null)
                .id(1)
                .username("testuser")
                .email("test@test.pl")
                .password("password")
                .build();

        userRepository.save(user);
    }


    @Test
    @WithMockUser(username = "testuser")
    void shouldDeleteUserAndRedirectToLogin() throws Exception {

        mockMvc.perform(get("/profile/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", endsWith("/login?deleted")));


        assertThat(userRepository.findByUsername("testuser")).isEmpty();
    }



    @Test
    void shouldRedirectToLoginWhenPrincipalIsNull() throws Exception {

        mockMvc.perform(get("/profile/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        assertThat(userRepository.findByUsername("testuser")).isPresent();
    }


    @Test
    @WithMockUser(username = "ghost")
    void shouldRedirectToLoginWhenUserNotFound() throws Exception {

        mockMvc.perform(get("/profile/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", endsWith("/login")));

    }

}
