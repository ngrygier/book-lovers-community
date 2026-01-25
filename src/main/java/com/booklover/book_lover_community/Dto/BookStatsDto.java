package com.booklover.book_lover_community.Dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class BookStatsDto {
    private Long readersCount;
    private Double averageRating;
    private Map<Integer, Long> ratingDistribution;


    public BookStatsDto(Long readers, double v, Map<Integer, Long> distribution) {
    }

}
