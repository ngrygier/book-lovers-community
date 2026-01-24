package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

    // Pobranie wszystkich bibliotek użytkownika
    List<Library> findByUserId(Long userId);

    // Pobranie jednej biblioteki użytkownika po nazwie
    Library findByUserIdAndName(Long userId, String name);

    Optional<Library> findByUserAndName(User user, String name);


}
