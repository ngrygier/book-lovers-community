package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Library;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.repository.LibraryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    // Pobranie biblioteki po ID
    public Library getLibraryById(Long id) {
        return libraryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Library not found"));
    }

    // Pobranie wszystkich bibliotek użytkownika
    public List<Library> getLibrariesByUser(User user) {
        return libraryRepository.findByUserId(Long.valueOf(user.getId()));
    }

    // Dodanie książki do biblioteki
    @Transactional
    public void addBookToLibrary(Library library, Book book) {
        library.getBooks().add(book);
        book.setLibrary(library);
        libraryRepository.save(library);
    }

    @Transactional
    public Library getLibraryByUserAndName(User user, String libraryName) {
        return libraryRepository.findByUserAndName(user, libraryName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Library '" + libraryName + "' not found for user " + user.getUsername()));
    }

}
