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

    @Transactional
    public void addBookToCustomLibrary(User currentUser, Long libraryId, Book book) {
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new RuntimeException("Biblioteka nie istnieje"));

        // Sprawdzenie właściciela
        if (!library.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Biblioteka nie należy do użytkownika");
        }

        // Dodanie książki do biblioteki
        library.getBooks().add(book);
        book.setLibrary(library); // jeśli w encji Book masz @ManyToOne do Library

        libraryRepository.save(library); // zapis relacji
    }

    @Transactional
    public void removeBookFromLibrary(User currentUser, Long libraryId, Long bookId) {
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new RuntimeException("Biblioteka nie istnieje"));

        // Sprawdzenie właściciela
        if (!library.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Biblioteka nie należy do użytkownika");
        }

        // Znalezienie książki w bibliotece
        Book bookToRemove = library.getBooks().stream()
                .filter(book -> book.getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Książka nie istnieje w tej bibliotece"));

        // Usunięcie książki z listy
        library.getBooks().remove(bookToRemove);

        // Jeśli w encji Book masz @ManyToOne do Library, możesz ustawić null
        bookToRemove.setLibrary(null);

        // Zapisanie zmian
        libraryRepository.save(library);
    }



}
