package com.booklover.book_lover_community.repository;
import com.booklover.book_lover_community.model.UserBook;
import com.booklover.book_lover_community.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    List<UserBook> findByBookId(Long bookId);
    Optional<UserBook> findByUserIdAndBookId(Integer userId, Long bookId);
    List<UserBook> findAllByUser(User user);

}
