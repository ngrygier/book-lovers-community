package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.Dto.BookStatsDto;
import com.booklover.book_lover_community.repository.LibraryRepository;
import com.booklover.book_lover_community.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookStatsService {

    private final ReviewRepository reviewRepository;
    private final LibraryRepository libraryRepository;

    public BookStatsService(ReviewRepository reviewRepository,
                            LibraryRepository libraryRepository) {
        this.reviewRepository = reviewRepository;
        this.libraryRepository = libraryRepository;
    }

    public BookStatsDto getBookStats(Long bookId) {

        Long readers = libraryRepository.countReaders(bookId);
        Double avgRating = reviewRepository.getAverageRating(bookId);
        if (avgRating == null) {
            avgRating = 0.0; // <- domyślna wartość jeśli brak ocen
        }
        Map<Integer, Long> distribution = new HashMap<>();
        for (Object[] row : reviewRepository.getRatingDistribution(bookId)) {
            distribution.put((Integer) row[0], (Long) row[1]);
        }

        return new BookStatsDto(
                readers,
                avgRating,
                distribution
        );
    }
}

