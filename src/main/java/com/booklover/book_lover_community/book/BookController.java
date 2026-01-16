package com.booklover.book_lover_community.book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    /*@GetMapping
    public List<Book> getBooks(){
        return service.allBooks();
    }
*/
    //@PostMapping("/books");
}
