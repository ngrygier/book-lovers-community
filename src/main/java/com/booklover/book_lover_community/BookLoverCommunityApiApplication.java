package com.booklover.book_lover_community;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@OpenAPIDefinition(
		info = @Info(title = "Book Lovers API", version = "1.0.0", description = "API for book lovers")
)
@SpringBootApplication
@EnableJpaAuditing
public class BookLoverCommunityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookLoverCommunityApiApplication.class, args);
	}

}
