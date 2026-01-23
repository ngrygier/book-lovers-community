package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.Dto.EditProfileDto;
import com.booklover.book_lover_community.Dto.RegisterRequestDto;
import com.booklover.book_lover_community.model.Role;
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
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    // Repozytorium do operacji na tabeli users w bazie danych
    private final UserRepository userRepository;

    // Encoder do bezpiecznego haszowania haseł
    private final PasswordEncoder passwordEncoder;

    // Konstruktor – Spring wstrzykuje zależności automatycznie
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Rejestracja nowego użytkownika
    // @Transactional gwarantuje, że zapis do bazy wykona się w całości albo wcale
    @Transactional
    public User registerUser(RegisterRequestDto request) {

        // Sprawdzenie czy username jest już zajęty
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Sprawdzenie czy email jest już używany
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists with given email");
        }

        // Tworzenie nowej encji użytkownika
        User user = new User();

        // Przepisanie danych z DTO do encji
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);

        // Haszowanie hasła przed zapisem do bazy
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Ustawienia wymagane przez Spring Security
        user.setEnabled(true);
        user.setAccountLocked(false);

        // Zapis użytkownika do bazy danych
        return userRepository.save(user);
    }

    // Pobranie użytkownika po ID
    @Transactional
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User with id " + id + " not found"));
    }

    // Pobranie aktualnie zalogowanego użytkownika z kontekstu bezpieczeństwa
    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new IllegalStateException("Brak zalogowanego użytkownika");
        }

        String username = auth.getName(); // to zwraca username zalogowanego
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found in DB"));
    }


    // Aktualizacja danych profilu użytkownika
    @Transactional
    public void updateProfile(EditProfileDto dto) throws IOException {

        // Pobranie aktualnie zalogowanego użytkownika
        User user = getCurrentUser();

        // Aktualizacja nazwy użytkownika
        user.setUsername(dto.getUsername());

        // Pobranie przesłanego pliku ze zdjęciem profilowym
        MultipartFile image = dto.getProfileImage();

        // Sprawdzenie czy plik istnieje i nie jest pusty
        if (image != null && !image.isEmpty()) {

            // Generowanie unikalnej nazwy pliku
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();

            // Ścieżka zapisu pliku na dysku
            Path uploadPath = Path.of("uploads/profile-images/" + filename);

            // Utworzenie katalogów, jeśli nie istnieją
            Files.createDirectories(uploadPath.getParent());

            // Zapis pliku na dysku
            Files.write(uploadPath, image.getBytes());

            // Zapis nazwy pliku w bazie danych
            user.setProfileImage(filename);
        }

        // Zapis zmian użytkownika w bazie
        userRepository.save(user);
    }

    // Metoda wymagana przez Spring Security podczas logowania
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Pobranie użytkownika po username
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User with username '" + username + "' not found"));
    }
}
