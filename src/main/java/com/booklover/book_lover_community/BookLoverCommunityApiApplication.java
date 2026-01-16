package com.booklover.book_lover_community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BookLoverCommunityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookLoverCommunityApiApplication.class, args);
	}

}
