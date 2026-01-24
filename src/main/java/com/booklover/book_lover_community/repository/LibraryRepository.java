package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

    // Pobranie wszystkich bibliotek użytkownika
    List<Library> findByUserId(Long userId);

    // Pobranie jednej biblioteki użytkownika po nazwie
    Library findByUserIdAndName(Long userId, String name);

}
