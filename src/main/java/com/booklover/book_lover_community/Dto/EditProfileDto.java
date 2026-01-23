package com.booklover.book_lover_community.Dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class EditProfileDto {
    private String username;
    private MultipartFile profileImage;
}
