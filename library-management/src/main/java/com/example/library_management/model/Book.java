package com.example.library_management.model;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String isbn;
    private boolean available;
    @Column(name = "published_year")  // explicit mapping to ensure mapped to SQL publish_year
    private Integer publishedYear; // new field added on 28May2025
    
    // Setter for ID — required for updating
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for ID
    public Long getId() {
        return id;
    }
    
    
    // Add other getters and setters here
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    // Getter and setting for the Addition of publish year field on 28May 2025
    public Integer getPublishedYear() {
        return publishedYear;
    }
    // Getter and setting for the Addition of publish year field on 28May 2025
    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }
    
    
}