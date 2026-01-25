package com.booklover.book_lover_community.service;

import com.booklover.book_lover_community.model.Review;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // Pobranie wszystkich recenzji użytkownika
    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findAllByUser(user);
    }

    // Zapis recenzji
    public Review save(Review review) {
        return reviewRepository.save(review);
    }
}
