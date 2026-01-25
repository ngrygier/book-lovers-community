package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.Dto.RegisterRequestDto;
import com.booklover.book_lover_community.repository.FakeUserRepository;
import com.booklover.book_lover_community.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private AuthenticationService authService;
    private UserRepository fakeUserRepo;
    private PasswordEncoder fakePasswordEncoder;

    @BeforeEach
    void setUp() {
        // Ręcznie podmieniamy zależności na nasze stuby/fake
        fakeUserRepo = new FakeUserRepository();
        fakePasswordEncoder = new FakePasswordEncoder();
        authService = new AuthenticationService(fakeUserRepo, fakePasswordEncoder);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setFirstname("Jan");
        dto.setLastname("Kowalski");
        dto.setUsername("janek");
        dto.setEmail("jan@example.com");
        dto.setPassword("12345");

        authService.register(dto);

        assertTrue(fakeUserRepo.existsByUsername("janek"));
        assertTrue(fakeUserRepo.existsByEmail("jan@example.com"));
    }

    @Test
    void shouldThrowExceptionForDuplicateUsername() {
        // Najpierw rejestracja poprawna
        RegisterRequestDto dto1 = new RegisterRequestDto();
        dto1.setFirstname("Anna");
        dto1.setLastname("Nowak");
        dto1.setUsername("anna");
        dto1.setEmail("anna@example.com");
        dto1.setPassword("123");
        authService.register(dto1);

        // Teraz próba rejestracji z tym samym username
        RegisterRequestDto dto2 = new RegisterRequestDto();
        dto2.setFirstname("Aneta");
        dto2.setLastname("Kowalska");
        dto2.setUsername("anna"); // duplikat
        dto2.setEmail("aneta@example.com");
        dto2.setPassword("321");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.register(dto2));
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForDuplicateEmail() {
        // Najpierw rejestracja poprawna
        RegisterRequestDto dto1 = new RegisterRequestDto();
        dto1.setFirstname("Piotr");
        dto1.setLastname("Zieliński");
        dto1.setUsername("piotr");
        dto1.setEmail("piotr@example.com");
        dto1.setPassword("abc");
        authService.register(dto1);

        // Teraz próba rejestracji z tym samym email
        RegisterRequestDto dto2 = new RegisterRequestDto();
        dto2.setFirstname("Paweł");
        dto2.setLastname("Kowal");
        dto2.setUsername("pawel");
        dto2.setEmail("piotr@example.com"); // duplikat
        dto2.setPassword("xyz");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.register(dto2));
        assertEquals("Email already exists", exception.getMessage());
    }
}
