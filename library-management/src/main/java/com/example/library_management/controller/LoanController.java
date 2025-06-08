// LoanController.java
package com.example.library_management.controller;

import com.example.library_management.dto.LoanSearchDTO;
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

    // ──────────────────────────────────────────────
    // ADMIN: Retrieve all loans in the system
    // ──────────────────────────────────────────────
    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    // ──────────────────────────────────────────────
    // MEMBER: Retrieve loans for current logged-in user
    // ──────────────────────────────────────────────
    @GetMapping("/my")
    public List<Loan> getMyLoans() {
        return loanService.getLoansForCurrentMember();
    }

    // ──────────────────────────────────────────────
    // MEMBER / ADMIN: Retrieve loan details by loan ID
    // ──────────────────────────────────────────────
    @GetMapping("/{id}")
    public Loan getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }

    // ──────────────────────────────────────────────
    // MEMBER: Borrow a book using book ID
    // ──────────────────────────────────────────────
    @PostMapping("/borrow")
    public Loan borrowBook(@RequestParam Long bookId) {
        return loanService.borrowBook(bookId);
    }

    // ──────────────────────────────────────────────
    // MEMBER: Return a book by loan ID
    // ──────────────────────────────────────────────
    @PutMapping("/{id}/return")
    public Loan returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }

    // ──────────────────────────────────────────────
    // MEMBER: Renew a loan by loan ID
    // ──────────────────────────────────────────────
    @PutMapping("/{id}/renew")
    public Loan renewLoan(@PathVariable Long id) {
        return loanService.renewLoan(id);
    }

    // ──────────────────────────────────────────────
    // ADMIN: Delete a loan by ID
    // ──────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public void deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
    }

    // ──────────────────────────────────────────────
    // ADMIN: Search loans by partial or full member name
    // ──────────────────────────────────────────────
    @GetMapping("/search")
    public List<LoanSearchDTO> searchLoansByMemberName(@RequestParam String name) {
        return loanService.searchLoansByMemberName(name);
    }
}
