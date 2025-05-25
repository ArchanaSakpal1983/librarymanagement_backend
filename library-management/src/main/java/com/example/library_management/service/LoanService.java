package com.example.library_management.service;

import com.example.library_management.model.Loan;
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

    // get all loan records
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // get loan record by id
    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    // save new loan record or update existing loan record
    public Loan saveLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    // delet loan record by ID
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
}
