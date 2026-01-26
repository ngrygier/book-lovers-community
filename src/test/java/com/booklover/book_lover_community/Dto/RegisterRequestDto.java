package com.booklover.book_lover_community.Dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

class RegisterRequestDtoTest {

    private RegisterRequestDto dto;

    @BeforeEach
    void setUp() {
        dto = new RegisterRequestDto();
    }

    @Test
    void shouldSetAndGetFirstname() {
        dto.setFirstname("Jan");
        assertThat(dto.getFirstname(), equalTo("Jan"));
    }

    @Test
    void shouldSetAndGetLastname() {
        dto.setLastname("Kowalski");
        assertThat(dto.getLastname(), equalTo("Kowalski"));
    }

    @Test
    void shouldSetAndGetUsername() {
        dto.setUsername("jankow");
        assertThat(dto.getUsername(), equalTo("jankow"));
    }

    @Test
    void shouldSetAndGetEmail() {
        dto.setEmail("jan.kowalski@test.pl");
        assertThat(dto.getEmail(), equalTo("jan.kowalski@test.pl"));
    }

    @Test
    void shouldSetAndGetPassword() {
        dto.setPassword("secret123");
        assertThat(dto.getPassword(), equalTo("secret123"));
    }

    @Test
    void shouldHaveAllProperties() {
        assertThat(dto, hasProperty("firstname"));
        assertThat(dto, hasProperty("lastname"));
        assertThat(dto, hasProperty("username"));
        assertThat(dto, hasProperty("email"));
        assertThat(dto, hasProperty("password"));
    }
}
