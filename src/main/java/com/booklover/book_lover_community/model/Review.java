package com.booklover.book_lover_community.model;

import com.booklover.book_lover_community.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Książka, której dotyczy recenzja
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Użytkownik, który napisał recenzję
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int stars;

    @Column(name="content", nullable = false, length = 1000)
    private String content;


}
