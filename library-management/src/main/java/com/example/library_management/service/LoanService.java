package com.example.library_management.service;

import com.example.library_management.dto.LoanSearchDTO;
import com.example.library_management.model.Book;
import com.example.library_management.model.Loan;
import com.example.library_management.model.Member;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.LoanRepository;
import com.example.library_management.service.MemberService;
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

    /**
     * ADMIN: Retrieve all loans in the system
     */
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    /**
     * MEMBER: Retrieve loans for the currently authenticated member
     */
    public List<Loan> getLoansForCurrentMember() {
        Member current = memberService.getCurrentAuthenticatedMember();
        return loanRepository.findByMember(current);
    }

    /**
     * ADMIN / MEMBER: Retrieve a single loan by its ID
     */
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    /**
     * MEMBER: Borrow a book by its ID (creates a new loan)
     */
    public Loan borrowBook(Long bookId) {
        Member member = memberService.getCurrentAuthenticatedMember();

        // Validate eligibility and book availability
        validateLoanCreation(member, bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Create new loan record
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(Loan.LOAN_DURATION_DAYS));
        loan.setRenewCount(0);

        // Mark book as unavailable
        book.setAvailable(false);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    /**
     * MEMBER: Return a book by setting the return date and calculating fine
     */
    public Loan returnBook(Long loanId) {
        Loan loan = getLoanById(loanId);

        if (loan.isReturned()) {
            throw new RuntimeException("Book already returned");
        }

        // Mark book as returned and update fine
        loan.setReturnDate(LocalDate.now());
        loan.setFineAmount(loan.calculateCurrentFine());

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    /**
     * MEMBER: Renew a loan if eligible (not overdue, not returned, under limit)
     */
    public Loan renewLoan(Long loanId) {
        Loan loan = getLoanById(loanId);

        if (loan.isReturned()) {
            throw new RuntimeException("Cannot renew a returned book.");
        }

        if (!loan.canRenew()) {
            throw new RuntimeException("Cannot renew this loan (limit reached or overdue).");
        }

        // Extend due date and increment renewal count
        loan.renew();
        return loanRepository.save(loan);
    }

    /**
     * ADMIN: Delete a loan by ID
     */
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    /**
     * ADMIN: Search loans by partial or full member name using DTO projection
     */
    public List<LoanSearchDTO> searchLoansByMemberName(String namePart) {
        return loanRepository.findByMemberNameContainingIgnoreCase(namePart);
    }

    /**
     * Validate business rules before creating a loan:
     * - Membership must be valid
     * - Member must not exceed borrowing limit
     * - Member must not have overdue books
     * - Book must be available
     */
    private void validateLoanCreation(Member member, Long bookId) {
        if (!memberService.isMembershipValid(member)) {
            throw new RuntimeException("Membership has expired.");
        }

        if (memberService.getActiveLoanCount(member) >= 3) {
            throw new RuntimeException("Borrowing limit exceeded. Max 3 books.");
        }

        if (memberService.hasOverdueBooks(member)) {
            throw new RuntimeException("You have overdue books. Please return them first.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is currently not available.");
        }
    }
}