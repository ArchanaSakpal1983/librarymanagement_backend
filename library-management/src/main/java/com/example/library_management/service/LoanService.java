package com.example.library_management.service;
// addition 29May book model for book availability logic
import com.example.library_management.model.Book;
import com.example.library_management.model.Loan;
// addition 29 May BookRepo for Autowire DB operations relating to availability logic
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Service layer for handling logic related to Loans (borrowing books).

@Service 	// Marks this class as a service bean
public class LoanService {

    @Autowired // Inject LoanRepository for DB operations
    private LoanRepository loanRepository;
    
    @Autowired // Inject BookRepository for DB operations - 29 May for availability logic
    private BookRepository bookRepository;

    // get all loan records
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // get loan record by id
    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    // SAVING / UPDATING LOAN RECORD (revised 29May to update availibility):
    // to perform the following fuctions
    // 1. Retrieve the related Book for DB
    // 2. Check is book is loaned or returned (availability)
    // 3. Update the available satus on the book.
    // 4. Save the Book to DB
    // 5. Linked the managed Book back to Loan.
    // 6. Finally Save the loan.
    public Loan saveLoan(Loan loan) {
        Book book = loan.getBook();

        if (book != null && book.getId() != null) {
            Optional<Book> bookOptional = bookRepository.findById(book.getId());
            if (bookOptional.isPresent()) {
                Book managedBook = bookOptional.get();

                // Check if the loan has a returnDate set (i.e., book is returned)
                if (loan.getReturnDate() != null) {
                    managedBook.setAvailable(true);
                } else {
                    // Book is being borrowed
                    managedBook.setAvailable(false);
                }

                bookRepository.save(managedBook);
                loan.setBook(managedBook); // ensure loan is linked to managed entity
            }
        }

        return loanRepository.save(loan);
    }

    // delet loan record by ID
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
}
