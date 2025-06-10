// BookRepository.java
package com.example.library_management.repository;

//Import the Book entity class
import com.example.library_management.model.Book;

//Import Spring Data JPA interface to provide CRUD operations
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//This interface below is for Book entity
//Extending JpaRepository that provide built-in methods for basic CRUD operations anf pagination 
//@param <Book> The entity type to manage
//@param <Long> The type of the entity's primary key

public interface BookRepository extends JpaRepository<Book, Long> {
	// Custom queries can go here if needed
	
	// Custom method to find a book by its ISBN
    Optional<Book> findByIsbn(String isbn);	
}

