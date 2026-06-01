package org.example.hometask.servise;

import org.example.hometask.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BookService {
    void createBook(Book newBook);
    Page<Book> getAllBooks(Pageable pageable);
    Book getBookById(UUID id);
    void updateBook(UUID id, Book book);
    void deleteBook(UUID id);
}
