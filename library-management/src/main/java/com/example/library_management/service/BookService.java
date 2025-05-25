package com.example.library_management.service;

import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


// Service layer for handling business logic related to Books.

@Service 		// Marks this class as a service bean
public class BookService {

    @Autowired // Injects BookRepository for data access
    private BookRepository bookRepository;

    // get a list of all the books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // get specific book by ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // create a new book record or update a book
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // delete book by ID
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
