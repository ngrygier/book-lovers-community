package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.repository.BookRepository;
import com.booklover.book_lover_community.user.ShelfStatus;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.repository.UserRepository;
import com.booklover.book_lover_community.model.UserBook;
import com.booklover.book_lover_community.repository.UserBookRepository;
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

 }

