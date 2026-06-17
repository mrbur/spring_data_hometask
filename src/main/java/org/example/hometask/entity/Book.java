package org.example.hometask.entity;

import java.util.UUID;

public record Book(UUID id, String title, String author, int publicationYear) {
    public Book {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название не должно быть пустым");
        }
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
