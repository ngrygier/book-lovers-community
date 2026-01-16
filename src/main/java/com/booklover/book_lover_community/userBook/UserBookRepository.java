package com.booklover.book_lover_community.userBook;
import com.booklover.book_lover_community.book.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    List<UserBook> findByBookId(Long bookId);
    Optional<UserBook> findByUserIdAndBookId(Integer userId, Long bookId);

}
