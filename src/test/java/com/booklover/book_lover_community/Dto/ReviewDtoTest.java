package com.booklover.book_lover_community.Dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

class ReviewDtoTest {

    private ReviewDto dto;

    @BeforeEach
    void setUp() {
        dto = new ReviewDto();
    }

    @Test
    void shouldSetAndGetStars() {
        dto.setStars(5);
        assertThat(dto.getStars(), equalTo(5));
    }

    @Test
    void shouldSetAndGetContent() {
        dto.setContent("Świetna książka!");
        assertThat(dto.getContent(), equalTo("Świetna książka!"));
    }

    @Test
    void shouldHaveAllProperties() {
        assertThat(dto, hasProperty("stars"));
        assertThat(dto, hasProperty("content"));
    }
}
