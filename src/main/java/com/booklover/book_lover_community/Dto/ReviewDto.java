package com.booklover.book_lover_community.Dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {

    @NotNull
    private Integer stars;


    @NotEmpty
    private String content;

}

