package com.booklover.book_lover_community.userBook;

import com.booklover.book_lover_community.book.Book;
import com.booklover.book_lover_community.user.ShelfStatus;
import com.booklover.book_lover_community.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_book")
@EntityListeners(AuditingEntityListener.class)
public class UserBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShelfStatus status; // TO_READ, READING, READ

    private Integer rating; // ocena od 1 do 10

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime addedDate;
}
