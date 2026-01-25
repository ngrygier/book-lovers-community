package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.model.Review;
import com.booklover.book_lover_community.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND YEAR(r.readDate) = :year")
    long countBooksReadByUserInYear(@Param("userId") Long userId, @Param("year") int year);

    @Query("SELECT AVG(r.stars) FROM Review r WHERE r.book.id = :bookId")
    Double getAverageRating(@Param("bookId") Long bookId);

    @Query("""
        SELECT r.stars, COUNT(r)
        FROM Review r
        WHERE r.book.id = :bookId
        GROUP BY r.stars
    """)
    List<Object[]> getRatingDistribution(@Param("bookId") Long bookId);

    List<Review> findAllByUser(User user);
}

