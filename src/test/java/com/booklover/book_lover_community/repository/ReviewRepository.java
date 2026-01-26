package com.booklover.book_lover_community.repository;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Review;
import com.booklover.book_lover_community.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstname("Maria")
                .lastname("Testowa")
                .username("maria")
                .email("maria@mail.com")
                .password("password")
                .enabled(true)
                .accountLocked(false)
                .build();
        user = userRepository.save(user);

        Author author = new Author();
        author.setFullName("Test Author");
        author = authorRepository.save(author);

        book = new Book();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book = bookRepository.save(book);
    }

    @Test
    void shouldFindReviewsByUserId() {
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setStars(5);
        review.setContent("XD");
        review.setReadDate(LocalDate.now()); // ustawiamy readDate ręcznie
        reviewRepository.save(review);

        List<Review> result = reviewRepository.findByUserId(user.getId().longValue());

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldCountBooksReadByUserInYear() {
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setStars(4);
        review.setContent("XD");
        review.setReadDate(LocalDate.of(2024, 2, 1)); // konieczne dla testu
        reviewRepository.save(review);

        long count = reviewRepository.countBooksReadByUserInYear(user.getId().longValue(), 2024);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCalculateAverageRatingForBook() {
        Review r1 = new Review();
        r1.setUser(user);
        r1.setBook(book);
        r1.setStars(4);
        r1.setContent("Good");
        r1.setReadDate(LocalDate.now());

        Review r2 = new Review();
        r2.setUser(user);
        r2.setBook(book);
        r2.setStars(2);
        r2.setContent("Bad");
        r2.setReadDate(LocalDate.now());

        reviewRepository.saveAll(List.of(r1, r2));

        Double avg = reviewRepository.getAverageRating(book.getId());

        assertThat(avg).isEqualTo(3.0);
    }

    @Test
    void shouldReturnRatingDistributionForBook() {
        Review r1 = new Review();
        r1.setUser(user);
        r1.setBook(book);
        r1.setStars(5);
        r1.setContent("Great");
        r1.setReadDate(LocalDate.now());

        Review r2 = new Review();
        r2.setUser(user);
        r2.setBook(book);
        r2.setStars(3);
        r2.setContent("Ok");
        r2.setReadDate(LocalDate.now());

        reviewRepository.saveAll(List.of(r1, r2));

        List<Object[]> distribution = reviewRepository.getRatingDistribution(book.getId());

        assertThat(distribution).hasSize(2);
    }

    @Test
    void shouldFindAllReviewsByUser() {
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setStars(5);
        review.setContent("XD");
        review.setReadDate(LocalDate.now());

        reviewRepository.save(review);

        List<Review> result = reviewRepository.findAllByUser(user);

        assertThat(result).hasSize(1);
    }
}
