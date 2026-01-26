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

        library.getBooks().remove(bookToRemove);

        libraryRepository.save(library);
    }
    @Transactional
    public void deleteLibrary(Long libraryId) {
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new IllegalArgumentException("Library not found"));


        // Usuń powiązania z książkami w tabeli łączącej
        for (Book book : library.getBooks()) {
            book.getLibraries().remove(library); // odłącz bibliotekę od książki
        }
        library.getBooks().clear(); // usuń wszystkie książki z biblioteki


        libraryRepository.delete(library);
    }



}
