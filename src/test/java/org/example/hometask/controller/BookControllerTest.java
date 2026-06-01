package org.example.hometask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hometask.entity.Book;
import org.example.hometask.servise.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BookService bookService;

    private UUID bookId;
    private Book testBook;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        testBook = new Book(bookId, "Thinking in Java", "Bruce Eckel", 2006);
    }

    @Test
    void shouldReturnPagedBooks() throws Exception {
        List<Book> books = List.of(testBook);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(books, pageable, 1);

        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookPage);

        mockMvc.perform(get("/api/v1/books")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(bookId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Thinking in Java"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldReturnBookById() throws Exception {
        when(bookService.getBookById(bookId)).thenReturn(testBook);

        mockMvc.perform(get("/api/v1/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId.toString()))
                .andExpect(jsonPath("$.title").value("Thinking in Java"));
    }

    @Test
    void shouldCreateBook() throws Exception {
        doNothing().when(bookService).createBook(any(Book.class));

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateBook() throws Exception {
        doNothing().when(bookService).updateBook(eq(bookId), any(Book.class));

        mockMvc.perform(put("/api/v1/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(bookId);

        mockMvc.perform(delete("/api/v1/books/{id}", bookId))
                .andExpect(status().isNoContent());
    }
}