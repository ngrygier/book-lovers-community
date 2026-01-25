package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Library> findByUser(User user);

    boolean existsByUserAndNameIgnoreCase(User user, String name);


    boolean existsByUserIdAndNameIgnoreCase(Integer id, String trim);

    @Query("""
    SELECT COUNT(l)
    FROM Library l
    JOIN l.books b
    WHERE b.id = :bookId
""")
    Long countReaders(@Param("bookId") Long bookId);

}
