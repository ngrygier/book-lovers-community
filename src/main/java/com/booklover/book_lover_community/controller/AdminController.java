package com.booklover.book_lover_community.controller;

import com.booklover.book_lover_community.model.Author;
import com.booklover.book_lover_community.model.Book;
import com.booklover.book_lover_community.service.AuthorService;
import com.booklover.book_lover_community.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final AuthorService authorService;

    public AdminController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @GetMapping
    public String admin(){
        return "/admin";
    }


    //  DODAWANE KSIĄŻKI
    @GetMapping("/books/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        return "admin/books/add";
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.save(book);
        return "redirect:/books";
    }


    // LISTA KSIĄŻEK
    @GetMapping("/books/index")
    public String booksList(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "admin/books/index"; // wskazuje na listę książek
    }

    //  USUWANIE KSIĄŻKI
    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/admin/books";
    }


    //  DODAWANIE AUTORA
    @GetMapping("/authors/add")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/authors/add";
    }

    @PostMapping("/authors/add")
    public String addAuthor(@ModelAttribute Author author) {
        authorService.save(author);
        return "redirect:/admin/authors";
    }

    //  LISTA AUTORÓW
    @GetMapping("/authors")
    public String authors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "admin/authors/index";
    }

    // USUWANIE AUTORA
    @GetMapping("/authors/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteById(id);
        return "redirect:/admin/authors"; // po usunięciu wracamy do listy autorów
    }




}
