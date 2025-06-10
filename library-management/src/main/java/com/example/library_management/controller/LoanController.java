package com.example.library_management.controller;

import com.example.library_management.dto.LoanSearchDTO;
import com.example.library_management.dto.CreateLoanRequest;
import com.example.library_management.model.Loan;
import com.example.library_management.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "http://localhost:5173")
public class LoanController {

    @Autowired
    private LoanService loanService;

    // ──────────────────────────────────────────────
    // ADMIN: Retrieve all loans in the system
    // ──────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    // ──────────────────────────────────────────────
    // MEMBER: Retrieve loans for current logged-in user
    // ──────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    @GetMapping("/my")
    public List<Loan> getMyLoans() {
        return loanService.getLoansForCurrentMember();
    }

    // ──────────────────────────────────────────────
    // MEMBER / ADMIN: Retrieve loan details by loan ID
    // ──────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    @GetMapping("/{id}")
    public Loan getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }

    // ──────────────────────────────────────────────
    // ADMIN: Create a new loan using member ID and ISBN
    // ──────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public ResponseEntity<Loan> createLoan(@RequestBody CreateLoanRequest request) {
        try {
            Loan newLoan = loanService.createLoan(request.getMemberId(), request.getIsbn());
            return ResponseEntity.ok(newLoan);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);  // Return proper error response
        }
    }

    // ──────────────────────────────────────────────
    // MEMBER: Borrow a book using book ID
    // ──────────────────────────────────────────────
    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/borrow")
    public ResponseEntity<Loan> borrowBook(@RequestParam Long bookId) {
        try {
            Loan loan = loanService.borrowBook(bookId);
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);  // Return proper error response
        }
    }

    // ──────────────────────────────────────────────
    // MEMBER / ADMIN: Return a book by loan ID
    // ──────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    @PutMapping("/{id}/return")
    public ResponseEntity<String> returnBook(@PathVariable Long id) {
        try {
            loanService.returnBook(id);
            return ResponseEntity.ok("Book returned successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error returning book: " + e.getMessage());  // Return error response
        }
    }

    // ──────────────────────────────────────────────
    // ADMIN: Renew a loan by loan ID (extend due date by 14 days)
    // ──────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    @PutMapping("/{id}/renew")
    public ResponseEntity<String> renewLoan(@PathVariable Long id) {
        try {
            loanService.renewLoan(id);
            return ResponseEntity.ok("Loan renewed successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("Renewal failed: " + e.getMessage());  // Handle case when renewal is not possible
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());  // Handle other exceptions
        }
    }

    // ──────────────────────────────────────────────
    // ADMIN: Delete a loan by ID
    // ──────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLoan(@PathVariable Long id) {
        try {
            loanService.deleteLoan(id);
            return ResponseEntity.ok("Loan deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to delete loan: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────
    // ADMIN: Search loans by partial or full member name
    // ──────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    @GetMapping("/search")
    public List<LoanSearchDTO> searchLoansByMemberName(@RequestParam String name) {
        return loanService.searchLoansByMemberName(name);
    }
}