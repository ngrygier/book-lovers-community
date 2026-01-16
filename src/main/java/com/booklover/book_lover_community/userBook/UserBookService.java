package com.booklover.book_lover_community.userBook;

import com.booklover.book_lover_community.book.Book;
import com.booklover.book_lover_community.book.BookRepository;
import com.booklover.book_lover_community.user.ShelfStatus;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class UserBookService {

    private final UserBookRepository userBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserBookService(UserBookRepository userBookRepository,
                           UserRepository userRepository,
                           BookRepository bookRepository) {
        this.userBookRepository = userBookRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public UserBook addBookToShelf(
            Integer userId,
            Long bookId,
            ShelfStatus status
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Book not found"));

        UserBook userBook = UserBook.builder()
                .user(user)
                .book(book)
                .status(status)
                .build();

        return userBookRepository.save(userBook);
    }

    @Transactional
    public void changeShelfStatus(Long userBookId, ShelfStatus newStatus) {

        UserBook userBook = userBookRepository.findById(userBookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("UserBook not found"));

        userBook.setStatus(newStatus);
    }

    @Transactional
    public void removeBookFromShelf(Long userBookId) {
        userBookRepository.deleteById(userBookId);
    }
}

