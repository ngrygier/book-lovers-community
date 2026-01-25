package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);


    boolean existsByUsername(String username);
}
