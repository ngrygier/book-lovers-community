package com.booklover.book_lover_community.model;

import com.booklover.book_lover_community.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "book_review")
public class BookReview {

    @Id
    @GeneratedValue
    private long Id;

    @Column
    public String review;

    @ManyToOne
    private User user;
}
