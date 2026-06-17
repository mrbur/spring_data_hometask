package org.example.hometask.servise;

import org.example.hometask.entity.Book;
import org.example.hometask.repository.BookRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(@NonNull BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void createBook(Book newBook) {
        if(newBook == null || newBook.title() == null) {
            throw new IllegalArgumentException("empty input param");
        }
        if(newBook.id() != null && bookRepository.findById(newBook.id()) != null) {
            throw new IllegalArgumentException("book exists with such id = " + newBook);
        }
        bookRepository.save(newBook);
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Book getBookById(UUID id) {
        if(id == null) {
            throw new IllegalArgumentException("empty input param");
        }
        var found = bookRepository.findById(id);
        if(found == null) {
            throw new NoSuchElementException("book not found");
        }
        return found;
    }

    @Override
    public void updateBook(UUID id, Book book) {
        if(id == null) {
            throw new IllegalArgumentException("empty input param");
        }
        bookRepository.updateBook(id, book);
    }

    @Override
    public void deleteBook(UUID id) {
        if(id == null) {
            throw new IllegalArgumentException("empty input param");
        }
        if(!bookRepository.deleteById(id)) {
            throw new NoSuchElementException("book not found");
        }
    }
}
