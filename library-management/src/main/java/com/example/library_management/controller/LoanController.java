
// LoanController.java
package com.example.library_management.controller;

import com.example.library_management.model.Loan;
import com.example.library_management.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    // ADMIN: View All Loans
    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    // MEMBER / ADMIN: Get Loan by ID
    @GetMapping("/{id}")
    public Loan getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }

    // MEMBER: Borrow a Book
    @PostMapping("/borrow")
    public Loan borrowBook(@RequestParam Long bookId) {
        return loanService.borrowBook(bookId);
    }

    // MEMBER: Return a Book
    @PutMapping("/{id}/return")
    public Loan returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }

    // MEMBER: Renew a Loan
    @PutMapping("/{id}/renew")
    public Loan renewLoan(@PathVariable Long id) {
        return loanService.renewLoan(id);
    }

    // ADMIN: Delete a loan
    @DeleteMapping("/{id}")
    public void deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
    }
}
