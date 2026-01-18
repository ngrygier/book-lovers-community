package com.booklover.book_lover_community.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
