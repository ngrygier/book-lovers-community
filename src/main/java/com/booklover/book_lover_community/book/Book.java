package com.booklover.book_lover_community.book;

import com.booklover.book_lover_community.userBook.UserBook;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column
    private String status; // "TO_READ", "READ", "READING"

    @Column
    private double rating; // średnia ocen

    @Column
    private int ratingCount; // liczba ocen

    @OneToMany(fetch =LAZY, mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserBook> userBooks;
}
