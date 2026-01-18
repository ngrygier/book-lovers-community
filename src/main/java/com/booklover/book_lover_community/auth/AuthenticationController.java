package com.booklover.book_lover_community.auth;

import com.booklover.book_lover_community.Dto.RegisterRequestDto;
import com.booklover.book_lover_community.user.UserService;
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

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequestDto());
        return "register";
    }

    @PostMapping("/auth/register")
    public String registerUser(
            @Valid @ModelAttribute("registerRequest") RegisterRequestDto request,
            BindingResult result,
            Model model
    ) {


        if (result.hasErrors()) {
            model.addAttribute("registerRequest", request); // <--- DODAJEMY
            return "register";
        }

        try {
            userService.registerUser(request);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("registerRequest", request); // <--- DODAJEMY
            return "register";
        }
    }
}
