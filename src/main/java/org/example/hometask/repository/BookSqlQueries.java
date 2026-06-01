package org.example.hometask.repository;

public interface BookSqlQueries {

    String INSERT_BOOK_SQL = """
        INSERT INTO books (id, title, author, publication_year)
        VALUES (:id, :title, :author, :publicationYear)
        """;

    String FIND_BOOK_BY_ID_SQL = """
        SELECT id, title, author, publication_year 
        FROM books 
        WHERE id = :id
        """;

    String COUNT_BOOKS_SQL = """
        SELECT COUNT(*) 
        FROM books
        """;

    String FIND_ALL_BOOKS_PAGED_SQL = """
        SELECT id, title, author, publication_year 
        FROM books 
        ORDER BY id 
        LIMIT :limit 
        OFFSET :offset
        """;

    String FIND_BOOKS_BY_AUTHOR_SQL = """
        SELECT id, title, author, publication_year 
        FROM books 
        WHERE author = :author
        """;

    String DELETE_BOOK_BY_ID_SQL = """
        DELETE FROM books 
        WHERE id = :id
        """;

    String UPDATE_BOOK_SQL = """
        UPDATE books 
        SET title = :title, 
            author = :author, 
            publication_year = :publicationYear 
        WHERE id = :id
        """;
}