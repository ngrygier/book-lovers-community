package com.booklover.book_lover_community.repository;

//  pobieranie książek z bazy danych

import com.booklover.book_lover_community.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitle(String title);
    boolean existsByTitle(String title);
    List<Book> findByAuthor_FullName(String fullName);
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT b FROM Book b WHERE LOWER(b.author.fullName) LIKE LOWER(CONCAT('%', :author, '%'))")
    List<Book> findByAuthorFullNameContainingIgnoreCase(String author);

    @Query("SELECT b FROM Book b")
    List<Book> findAllBooks();




}
