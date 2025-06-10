// LoanService.java
package com.example.library_management.service;

import com.example.library_management.dto.LoanSearchDTO;
import com.example.library_management.model.Book;
import com.example.library_management.model.Loan;
import com.example.library_management.model.Member;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberService memberService;

    // ──────────────────────────────────────────────
    // ADMIN: Retrieve all loans in the system
    // ──────────────────────────────────────────────
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // ──────────────────────────────────────────────
    // MEMBER: Retrieve loans for current logged-in user
    // ──────────────────────────────────────────────
    public List<Loan> getLoansForCurrentMember() {
        Member current = memberService.getCurrentAuthenticatedMember();
        return loanRepository.findByMember(current);
    }

    // ──────────────────────────────────────────────
    // ADMIN / MEMBER: Retrieve loan by ID
    // ──────────────────────────────────────────────
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    // ──────────────────────────────────────────────
    // MEMBER: Borrow book by ID
    // ──────────────────────────────────────────────
    public Loan borrowBook(Long bookId) {
        Member member = memberService.getCurrentAuthenticatedMember();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return createLoan(member, book);
    }

    // ──────────────────────────────────────────────
    // ADMIN: Create loan using memberId and ISBN
    // ──────────────────────────────────────────────
    public Loan createLoan(Long memberId, String isbn) {
        Member member = memberService.getMemberById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Book with given ISBN not found"));

        return createLoan(member, book);
    }

    // ──────────────────────────────────────────────
    // INTERNAL: Create loan logic
    // ──────────────────────────────────────────────
    private Loan createLoan(Member member, Book book) {
        validateLoanCreation(member, book);

        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBook(book);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(Loan.LOAN_DURATION_DAYS));
        loan.setRenewCount(0);

        book.setAvailable(false);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    // ──────────────────────────────────────────────
    // MEMBER: Return book
    // ──────────────────────────────────────────────
    public Loan returnBook(Long loanId) {
        Loan loan = getLoanById(loanId);

        if (loan.isReturned()) {
            throw new RuntimeException("Book already returned");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setFineAmount(loan.calculateCurrentFine());

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    // ──────────────────────────────────────────────
    // ADMIN: Renew a loan by loan ID (extend due date by 14 days)
    // ──────────────────────────────────────────────
    public Loan renewLoan(Long loanId) {
        Loan loan = getLoanById(loanId);

        if (loan.isReturned()) {
            throw new RuntimeException("Cannot renew a returned book.");
        }

        if (!loan.canRenew()) {
            throw new RuntimeException("Cannot renew this loan (limit reached or overdue).");
        }

        // Extend the due date by 14 days
        loan.setDueDate(loan.getDueDate().plusDays(14));
        loan.setRenewCount(loan.getRenewCount() + 1); // Increment the renew count

        return loanRepository.save(loan);
    }

    // ──────────────────────────────────────────────
    // ADMIN: Delete loan
    // ──────────────────────────────────────────────
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    // ──────────────────────────────────────────────
    // ADMIN: Search by member name
    // ──────────────────────────────────────────────
    public List<LoanSearchDTO> searchLoansByMemberName(String namePart) {
        return loanRepository.findByMemberNameContainingIgnoreCase(namePart);
    }

    // ──────────────────────────────────────────────
    // INTERNAL: Validate before creating loan
    // ──────────────────────────────────────────────
    private void validateLoanCreation(Member member, Book book) {
        if (!memberService.isMembershipValid(member)) {
            throw new RuntimeException("Membership has expired.");
        }

        if (memberService.getActiveLoanCount(member) >= 3) {
            throw new RuntimeException("Borrowing limit exceeded. Max 3 books.");
        }

        if (memberService.hasOverdueBooks(member)) {
            throw new RuntimeException("This member has overdue books.");
        }

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available.");
        }
    }
}