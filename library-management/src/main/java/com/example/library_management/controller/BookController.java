package com.example.library_management.controller;

import com.example.library_management.model.Book;
import com.example.library_management.service.BookService;
import com.example.library_management.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing books.
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:5173") // allow frontend access during development
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    // ─────────────────────────────────────────────
    // ADMIN: Get all books
    // ─────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    // ─────────────────────────────────────────────
    // ADMIN: Get book by ID
    // ─────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────
    // ADMIN: Get book by ISBN
    // ─────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────
    // ADMIN: Add new book
    // ─────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    // ─────────────────────────────────────────────
    // ADMIN: Update book
    // ─────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        return bookService.saveBook(book);
    }

    // ─────────────────────────────────────────────
    // ADMIN: Delete book
    // ─────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}