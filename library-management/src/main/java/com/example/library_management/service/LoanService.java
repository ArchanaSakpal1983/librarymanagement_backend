// LoanService.java
package com.example.library_management.service;

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

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    public Loan borrowBook(Long bookId) {
        Member member = memberService.getCurrentAuthenticatedMember();

        validateLoanCreation(member, bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(Loan.LOAN_DURATION_DAYS));
        loan.setRenewCount(0);

        book.setAvailable(false);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

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

    public Loan renewLoan(Long loanId) {
        Loan loan = getLoanById(loanId);

        if (loan.isReturned()) {
            throw new RuntimeException("Cannot renew a returned book.");
        }

        if (!loan.canRenew()) {
            throw new RuntimeException("Cannot renew this loan (limit reached or overdue).");
        }

        try {
            loan.renew();
        } catch (IllegalStateException e) {
            throw new RuntimeException("Renewal failed: " + e.getMessage());
        }

        return loanRepository.save(loan);
    }

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