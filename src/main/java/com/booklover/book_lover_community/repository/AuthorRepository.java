package com.booklover.book_lover_community.repository;


import com.booklover.book_lover_community.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByFullName(String fullName);
    boolean existsByFullName(String fullName);
    void deleteById(Long id);

}
