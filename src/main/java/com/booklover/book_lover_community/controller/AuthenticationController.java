package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.Dto.RegisterRequestDto;
import com.booklover.book_lover_community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller

@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    // Obsługa żądania GET na adres /register
    // Wyświetla formularz rejestracji użytkownika
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // Dodajemy do modelu nowy obiekt RegisterRequestDto
        // Dzięki temu Thymeleaf będzie wiedział, do czego bindować pola formularza
        model.addAttribute("registerRequest", new RegisterRequestDto());
        return "register"; // zwracamy nazwę widoku register.html
    }

    // Obsługa żądania POST na adres /auth/register
    // To jest miejsce, gdzie formularz rejestracji wysyła dane
    @PostMapping("/auth/register")
    public String registerUser(
            @Valid @ModelAttribute("registerRequest") RegisterRequestDto request, // Dane z formularza
            BindingResult result, // Obiekt, w którym Spring przechowuje wyniki walidacji
            Model model // Model do przekazania danych do widoku
    ) {

        // Sprawdzenie, czy wystąpiły błędy walidacji (np. puste pola lub złe email)
        if (result.hasErrors()) {
            // Jeśli są błędy, dodajemy ponownie dane użytkownika do modelu
            // żeby formularz mógł wyświetlić wpisane wartości
            model.addAttribute("registerRequest", request);
            return "register"; // wracamy do formularza
        }

        try {
            // Wywołanie metody serwisu, która zapisuje użytkownika w bazie
            userService.registerUser(request);

            // Po udanej rejestracji przekierowujemy użytkownika
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            // Jeśli serwis zgłosi wyjątek (np. username zajęty), dodajemy komunikat do modelu
            model.addAttribute("errorMessage", e.getMessage());

            // Dodajemy ponownie wprowadzone dane, żeby formularz nie był pusty
            model.addAttribute("registerRequest", request);
            return "register"; // wracamy do formularza z komunikatem błędu
        }
    }
}
