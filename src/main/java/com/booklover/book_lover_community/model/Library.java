package com.booklover.book_lover_community.model;

import com.booklover.book_lover_community.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // np. "Fantastyka", "Historia" itd.

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "library")
    private List<Book> books = new ArrayList<>();
}