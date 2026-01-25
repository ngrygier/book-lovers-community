package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.repository.ReviewRepository;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.repository.UserRepository;
import com.booklover.book_lover_community.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.ServletException;



import java.security.Principal;
import java.time.Year;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService; // jeśli masz metody pomocnicze
    private final HttpServletRequest request;

    @GetMapping("/profile/delete")
    public String deleteAccount(Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Usuń użytkownika z bazy
        userRepository.delete(user);

        // Wylogowanie po usunięciu konta
        try {
            request.logout();
        } catch (ServletException e) {
            e.printStackTrace();
        }

        // Przekierowanie do strony logowania
        return "redirect:/login?deleted";
    }


}

