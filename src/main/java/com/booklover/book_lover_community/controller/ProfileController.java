package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.Dto.EditProfileDto;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.repository.ReviewRepository;
import com.booklover.book_lover_community.repository.UserRepository;
import com.booklover.book_lover_community.service.UserService;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.repository.LibraryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Year;
import java.util.Arrays;
import java.util.List;

@Controller
public class ProfileController {

    private final UserService userService;
    private final LibraryRepository libraryRepository;
    private final ReviewRepository reviewRepository;

    public ProfileController(UserService userService,
                             LibraryRepository libraryRepository,
                             ReviewRepository reviewRepository) {
        this.userService = userService;
        this.libraryRepository = libraryRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        // ✅ aktualnie zalogowany użytkownik
        User user = userService.getCurrentUser();

        // DTO do edycji profilu
        EditProfileDto dto = new EditProfileDto();
        dto.setUsername(user.getUsername());

        // domyślne biblioteki
        List<Library> defaultLibraries = userService.getDefaultLibraries(user);

        // własne biblioteki
        List<Library> customLibraries = libraryRepository
                .findByUserId(Long.valueOf(user.getId()))
                .stream()
                .filter(lib -> !Arrays.asList("TO_READ", "READING", "READ")
                        .contains(lib.getName()))
                .toList();

        // 🔥 LICZENIE PRZECZYTANYCH KSIĄŻEK W ROKU
        int year = Year.now().getValue();

        long booksRead = reviewRepository
                .countBooksReadByUserInYear(Long.valueOf(user.getId()), year);

        user.setBooksReadThisYear(booksRead);

        // model
        model.addAttribute("user", user);
        model.addAttribute("editProfile", dto);
        model.addAttribute("defaultLibraries", defaultLibraries);
        model.addAttribute("customLibraries", customLibraries);

        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("editProfile") EditProfileDto dto)
            throws Exception {
        userService.updateProfile(dto);
        return "redirect:/profile";
    }

    @PostMapping("/profile/libraries/add")
    public String addLibrary(@ModelAttribute("name") String name) {

        User user = userService.getCurrentUser();

        if (name == null || name.trim().isEmpty()) {
            return "redirect:/profile";
        }

        boolean exists = libraryRepository
                .existsByUserIdAndNameIgnoreCase(user.getId(), name.trim());

        if (exists) {
            return "redirect:/profile";
        }

        Library library = new Library();
        library.setName(name.trim());
        library.setUser(user);

        libraryRepository.save(library);

        return "redirect:/profile";
    }
}
