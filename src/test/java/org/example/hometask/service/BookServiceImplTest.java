package org.example.hometask.service;

import org.example.hometask.entity.Book;
import org.example.hometask.repository.BookRepository;
import org.example.hometask.servise.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование BookServiceImpl")
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book sampleBook;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        sampleBook = new Book(bookId, "Clean Code", "Robert Martin", 2008);
    }

    @Test
    @DisplayName("createBook — успешное создание новой книги")
    void shouldCreateBookSuccessfully() {
        when(bookRepository.findById(bookId)).thenReturn(null);

        bookService.createBook(sampleBook);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(sampleBook);
    }

    @Test
    @DisplayName("createBook — ошибка, если книга с таким ID уже существует")
    void shouldThrowExceptionWhenCreatingDuplicateBook() {
        // Arrange
        // Имитируем, что книга в базе уже есть
        when(bookRepository.findById(bookId)).thenReturn(sampleBook);

        // Act & Assert
        assertThatThrownBy(() -> bookService.createBook(sampleBook))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("book exists with such id");

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any()); // Сохранение не должно вызываться
    }

    @Test
    @DisplayName("getBookById — успешное получение книги по ID")
    void shouldReturnBookWhenIdExists() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(sampleBook);

        // Act
        Book actualBook = bookService.getBookById(bookId);

        // Assert
        assertThat(actualBook).isEqualTo(sampleBook);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("getBookById — ошибка 404, если репозиторий вернул null")
    void shouldThrowExceptionWhenBookNotFound() {
        when(bookRepository.findById(bookId)).thenReturn(null);

        assertThatThrownBy(() -> bookService.getBookById(bookId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("book not found");

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("getAllBooks — получение страницы книг")
    void shouldReturnPagedBooks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> expectedPage = new PageImpl<>(List.of(sampleBook));
        when(bookRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Book> actualPage = bookService.getAllBooks(pageable);

        // Assert
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent()).containsExactly(sampleBook);
    }
}