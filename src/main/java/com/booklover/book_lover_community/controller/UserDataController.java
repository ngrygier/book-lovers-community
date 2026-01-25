package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.model.Review;
import com.booklover.book_lover_community.user.User;
import com.booklover.book_lover_community.service.BookService;
import com.booklover.book_lover_community.service.ReviewService;
import com.booklover.book_lover_community.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/data")
public class UserDataController {

    private final UserService userService;
    private final BookService bookService;
    private final ReviewService reviewService;

    public UserDataController(UserService userService, BookService bookService, ReviewService reviewService) {
        this.userService = userService;
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    // ================= EXPORT JSON =================
    @GetMapping("/export/json/{userId}")
    public ResponseEntity<?> exportUserDataJson(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        List<Book> books = bookService.getBooksByUser(user);
        List<Review> reviews = reviewService.getReviewsByUser(user);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("books", books);
        data.put("reviews", reviews);

        return ResponseEntity.ok(data);
    }

    // ================= EXPORT CSV =================
    @GetMapping("/export/csv/{userId}")
    public void exportUserDataCsv(@PathVariable Long userId, HttpServletResponse response) throws IOException {
        User user = userService.getUserById(userId);
        List<Book> books = bookService.getBooksByUser(user);
        List<Review> reviews = reviewService.getReviewsByUser(user);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"user_data.csv\"");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Book Title,Author,Review,Rating");

            for (Book book : books) {
                String reviewText = reviews.stream()
                        .filter(r -> r.getBook().getId().equals(book.getId()))
                        .map(Review::getContent)
                        .findFirst().orElse("");
                String rating = reviews.stream()
                        .filter(r -> r.getBook().getId().equals(book.getId()))
                        .map(r -> String.valueOf(r.getStars()))
                        .findFirst().orElse("");

                // Zamiana przecinka na średnik w treści, żeby CSV nie zepsuło kolumn
                reviewText = reviewText.replace(",", ";");

                writer.println(book.getTitle() + "," + book.getAuthor() + "," + reviewText + "," + rating);
            }
        }
    }

    // ================= IMPORT JSON =================
    @PostMapping("/import/json/{userId}")
    public ResponseEntity<?> importUserDataJson(@PathVariable Long userId, @RequestParam("file") MultipartFile file) throws IOException {
        User user = userService.getUserById(userId);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.readValue(file.getInputStream(), Map.class);

        // Import książek
        List<Map<String, Object>> books = (List<Map<String, Object>>) data.get("books");
        for (Map<String, Object> b : books) {
            Book book = new Book();
            book.setTitle((String) b.get("title"));
            bookService.save(book);
        }

        // Import recenzji
        List<Map<String, Object>> reviews = (List<Map<String, Object>>) data.get("reviews");
        for (Map<String, Object> r : reviews) {
            Review review = new Review();
            // Pobieramy książkę po ID
            Integer bookIdInt = (Integer) r.get("bookId"); // JSON może zwracać Integer
            if (bookIdInt == null) continue;

            bookService.findById(Long.valueOf(bookIdInt)).ifPresent(book -> {
                review.setBook(book);
                review.setContent((String) r.get("text"));
                review.setStars((Integer) r.get("rating"));
                review.setUser(user);
                reviewService.save(review);
            });

        }

        return ResponseEntity.ok("Dane zostały zaimportowane");
    }
}
