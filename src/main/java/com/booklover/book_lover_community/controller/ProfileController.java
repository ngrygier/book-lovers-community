package com.booklover.book_lover_community.controller;

import org.springframework.ui.Model;

import com.booklover.book_lover_community.Dto.EditProfileDto;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = userService.getCurrentUser();

        EditProfileDto dto = new EditProfileDto();
        dto.setUsername(user.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("editProfile", dto);
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("editProfile") EditProfileDto dto) throws Exception {
        userService.updateProfile(dto);
        return "redirect:/profile";
    }
}
