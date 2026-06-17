package org.example.hometask.repository;

import org.example.hometask.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.example.hometask.repository.BookSqlQueries.*;

@Repository
public class BookRepository {

    private final DataClassRowMapper<Book> bookMapper = DataClassRowMapper.newInstance(Book.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BookRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", book.id(), Types.OTHER)
                .addValue("title", book.title())
                .addValue("author", book.author())
                .addValue("publicationYear", book.publicationYear());

        jdbcTemplate.update(INSERT_BOOK_SQL, params);
    }

    public Book findById(UUID id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id, Types.OTHER);

        List<Book> books = jdbcTemplate.query(FIND_BOOK_BY_ID_SQL, params, bookMapper);
        return books.isEmpty() ? null : books.get(0);
    }

    public Page<Book> findAll(Pageable pageable) {
        Integer total = jdbcTemplate.queryForObject(COUNT_BOOKS_SQL, new MapSqlParameterSource(), Integer.class);
        if (total == null || total == 0) {
            return Page.empty(pageable);
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        List<Book> books = jdbcTemplate.query(FIND_ALL_BOOKS_PAGED_SQL, params, bookMapper);
        return new PageImpl<>(books, pageable, total);
    }

    public boolean deleteById(UUID id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id, Types.OTHER);

        int rowsAffected = jdbcTemplate.update(DELETE_BOOK_BY_ID_SQL, params);
        return rowsAffected > 0;
    }

    public void updateBook(UUID id, Book newBook) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id, Types.OTHER)
                .addValue("title", newBook.title())
                .addValue("author", newBook.author())
                .addValue("publicationYear", newBook.publicationYear());

        int rowsAffected = jdbcTemplate.update(UPDATE_BOOK_SQL, params);

        if (rowsAffected == 0) {
            throw new NoSuchElementException("Книга с ID " + newBook.id() + " не найдена.");
        }
    }
}
