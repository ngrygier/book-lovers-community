package com.booklover.book_lover_community.service;

import org.springframework.security.crypto.password.PasswordEncoder;


public class FakePasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return "ENC(" + rawPassword + ")";
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }
}
