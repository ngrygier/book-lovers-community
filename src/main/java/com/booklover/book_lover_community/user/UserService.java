package com.booklover.book_lover_community.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User already exists with given email");
        }

        //hashowanie hasła
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // wartości systemowe
        user.setEnabled(true);
        user.setAccountLocked(false);

        return userRepository.save(user);
    }

    @Transactional
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Transactional
    public User updateUser(Integer id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User with id " + id + " not found"));

        existingUser.setFirstname(updatedUser.getFirstname());
        existingUser.setLastname(updatedUser.getLastname());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());

        return userRepository.save(existingUser);
    }
}
