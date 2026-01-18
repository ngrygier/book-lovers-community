package com.booklover.book_lover_community.book;

//  pobieranie książek z bazy danych

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitle(String title);
    boolean existsByTitle(String title);
    List<Book> findByAuthor_FullName(String fullName);
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> getBookById(Long id);
}
