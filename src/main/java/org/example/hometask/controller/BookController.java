package org.example.hometask.controller;
import org.example.hometask.entity.Book;
import org.example.hometask.servise.BookService;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService BookService) {
        this.bookService = BookService;
    }

    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @PageableDefault(page = 0, size = 10, sort = "bookId") Pageable pageable) {
        Page<Book> BooksPage = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(BooksPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable UUID id) {
        Book Book = bookService.getBookById(id);

        return ResponseEntity.ok(Book);
    }

    @PostMapping
    public ResponseEntity createBook(@RequestBody @NonNull Book book) {
        bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBook(@PathVariable UUID id, @RequestBody @NonNull Book book) {
        bookService.updateBook(id, book);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}