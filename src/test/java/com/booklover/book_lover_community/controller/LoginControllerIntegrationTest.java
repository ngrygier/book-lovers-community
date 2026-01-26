package com.booklover.book_lover_community.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLoginPageWithoutParams() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeDoesNotExist("message"));
    }

    @Test
    void testLoginPageWithError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(result -> {
                    String error = (String) result.getModelAndView().getModel().get("error");
                    assertThat(error).isEqualTo("Invalid username or password");
                });
    }

    @Test
    void testLoginPageWithLogout() throws Exception {
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("message"))
                .andExpect(result -> {
                    String message = (String) result.getModelAndView().getModel().get("message");
                    assertThat(message).isEqualTo("You have been logged out successfully");
                });
    }

    @Test
    void testLoginPageWithErrorAndLogout() throws Exception {
        mockMvc.perform(get("/login").param("error", "true").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("message"))
                .andExpect(result -> {
                    String error = (String) result.getModelAndView().getModel().get("error");
                    String message = (String) result.getModelAndView().getModel().get("message");
                    assertThat(error).isEqualTo("Invalid username or password");
                    assertThat(message).isEqualTo("You have been logged out successfully");
                });
    }
}
