package com.example.library_management.repository;

//Import the Loan entity class
import com.example.library_management.model.Loan;

//Import Spring Data JPA interface to provide CRUD operations
import org.springframework.data.jpa.repository.JpaRepository;

// This interface below is for Loan entity
// Extending JpaRepository that provide built-in methods for basic CRUD operations anf pagination 
// @param <Loan> The entity type to manage
// @param <Long> The type of the entity's primary key

public interface LoanRepository extends JpaRepository<Loan, Long> {
	// Custom queries can go here if needed
}
