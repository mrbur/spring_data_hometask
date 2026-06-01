package org.example.hometask.repository;

import org.example.hometask.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import(BookRepository.class)
@DisplayName("Тестирование BookRepository")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = new Book(null, "Thinking in Java", "Bruce Eckel", 2006);
    }

    @Test
    @DisplayName("Успешное сохранение и поиск книги по ID")
    void shouldSaveAndFindBookById() {
        bookRepository.save(sampleBook);
        var foundBook = bookRepository.findById(sampleBook.id());

        assertThat(foundBook).isNotNull();
        assertThat(foundBook.title()).isEqualTo("Thinking in Java");
        assertThat(foundBook.author()).isEqualTo("Bruce Eckel");
        assertThat(foundBook.publicationYear()).isEqualTo(2006);
    }

    @Test
    @DisplayName("Поиск всех книг с пагинацией")
    void shouldFindAllBooksWithPagination() {
        Book book2 = new Book(null, "Effective Java", "Joshua Bloch", 2018);
        bookRepository.save(sampleBook);
        bookRepository.save(book2);

        Pageable pageable = PageRequest.of(0, 1);

        Page<Book> bookPage = bookRepository.findAll(pageable);

        assertThat(bookPage.getTotalElements()).isEqualTo(2);
        assertThat(bookPage.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Успешное удаление книги по ID")
    void shouldDeleteBookById() {
        bookRepository.save(sampleBook);

        boolean isDeleted = bookRepository.deleteById(sampleBook.id());
        var foundBook = bookRepository.findById(sampleBook.id());

        assertThat(isDeleted).isTrue();
        assertThat(foundBook).isNull();
    }

    @Test
    @DisplayName("Возврат false при удалении несуществующей книги")
    void shouldReturnFalseWhenDeletingNonExistingBook() {
        boolean isDeleted = bookRepository.deleteById(UUID.randomUUID());

        assertThat(isDeleted).isFalse();
    }

    @Test
    @DisplayName("Успешное обновление данных книги")
    void shouldUpdateBook() {
        bookRepository.save(sampleBook);
        Book updatedBook = new Book(sampleBook.id(), "Thinking in Java (4th Edition)", "Bruce Eckel", 2007);

        bookRepository.updateBook(sampleBook.id(), updatedBook);
        var foundBook = bookRepository.findById(sampleBook.id());

        assertThat(foundBook).isNotNull();
        assertThat(foundBook.title()).isEqualTo("Thinking in Java (4th Edition)");
        assertThat(foundBook.publicationYear()).isEqualTo(2007);
    }

    @Test
    @DisplayName("Выброс исключения при обновлении несуществующей книги")
    void shouldThrowExceptionWhenUpdatingNonExistingBook() {
        var id = UUID.randomUUID();
        Book nonExistingBook = new Book(id, "Fake Book", "Fake Author", 2026);

        assertThatThrownBy(() -> bookRepository.updateBook(id, nonExistingBook))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Книга с ID " + id + " не найдена.");
    }

    @Test
    void shouldNotThrowExceptionWhenGetNonExistingBook() {
        var id = UUID.randomUUID();
        var foundBook = bookRepository.findById(id);
        assertThat(foundBook).isNull();
    }
}