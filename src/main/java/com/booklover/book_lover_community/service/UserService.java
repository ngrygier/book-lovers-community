package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.Dto.EditProfileDto;
import com.booklover.book_lover_community.Dto.RegisterRequestDto;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.model.Role;
import com.booklover.book_lover_community.repository.LibraryRepository;
import com.booklover.book_lover_community.user.ShelfStatus;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LibraryRepository libraryRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       LibraryRepository libraryRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.libraryRepository = libraryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // -------------------- REJESTRACJA --------------------
    @Transactional
    public User registerUser(RegisterRequestDto request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists with given email");
        }

        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setAccountLocked(false);

        User savedUser = userRepository.save(user);

        // Tworzymy od razu trzy domyślne półki
        createDefaultShelves(savedUser);

        return savedUser;
    }

    // -------------------- DOMYŚLNE PÓŁKI --------------------
    @Transactional
    public void createDefaultShelves(User user) {
        for (ShelfStatus status : ShelfStatus.values()) {
            Library existing = libraryRepository.findByUserIdAndName(Long.valueOf(user.getId()), status.name());
            if (existing == null) {
                Library lib = new Library();
                lib.setName(status.name());
                lib.setUser(user);
                libraryRepository.save(lib);
            }
        }
    }

    // Pobranie domyślnych bibliotek użytkownika (tworzy je, jeśli nie istnieją)
    @Transactional
    public List<Library> getDefaultLibraries(User user) {
        List<Library> defaultLibraries = new ArrayList<>();

        for (ShelfStatus status : ShelfStatus.values()) {
            Library library = libraryRepository.findByUserIdAndName(Long.valueOf(user.getId()), status.name());
            if (library == null) {
                library = new Library();
                library.setName(status.name());
                library.setUser(user);
                libraryRepository.save(library);
            }
            defaultLibraries.add(library);
        }

        return defaultLibraries;
    }

    //tworzenie custom libraries
    @Transactional
    public Library createCustomLibrary(String name, User user) {
        if (libraryRepository.findByUserIdAndName(Long.valueOf(user.getId()), name) != null) {
            throw new RuntimeException("Biblioteka o tej nazwie już istnieje dla użytkownika");
        }

        Library library = new Library();
        library.setName(name);
        library.setUser(user);

        return libraryRepository.save(library);
    }


    // -------------------- AKTUALNY UŻYTKOWNIK --------------------
    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("Brak zalogowanego użytkownika");

        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found in DB"));
    }

    // -------------------- AKTUALIZACJA PROFILU --------------------
    @Transactional
    public void updateProfile(EditProfileDto dto) throws IOException {

        User user = getCurrentUser();
        user.setUsername(dto.getUsername());

        MultipartFile image = dto.getProfileImage();
        if (image != null && !image.isEmpty()) {

            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Path.of("uploads/profile-images/" + filename);
            Files.createDirectories(uploadPath.getParent());
            Files.write(uploadPath, image.getBytes());

            user.setProfileImage(filename);
        }

        userRepository.save(user);
    }

    // -------------------- SPRING SECURITY --------------------
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with username '" + username + "' not found"));
    }

}
