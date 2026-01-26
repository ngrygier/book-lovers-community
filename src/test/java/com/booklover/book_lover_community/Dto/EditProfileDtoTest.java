package com.booklover.book_lover_community.Dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.mock;

class EditProfileDtoTest {

    private EditProfileDto dto;

    @BeforeEach
    void setUp() {
        dto = new EditProfileDto();
    }

    @Test
    void shouldSetAndGetUsername() {
        // given
        String username = "testUser";

        // when
        dto.setUsername(username);

        // then
        assertThat(dto.getUsername(), equalTo(username));
    }

    @Test
    void shouldSetAndGetProfileImage() {
        // given
        MultipartFile multipartFile = mock(MultipartFile.class);

        // when
        dto.setProfileImage(multipartFile);

        // then
        assertThat(dto.getProfileImage(), equalTo(multipartFile));
    }

    @Test
    void shouldHaveUsernameProperty() {
        // then
        assertThat(dto, hasProperty("username"));
    }

    @Test
    void shouldHaveProfileImageProperty() {
        // then
        assertThat(dto, hasProperty("profileImage"));
    }
}
