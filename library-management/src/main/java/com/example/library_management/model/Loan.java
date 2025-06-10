// Loan.java

package com.example.library_management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Loan {

    public static final int MAX_RENEWALS = 2;
    public static final int LOAN_DURATION_DAYS = 14;
    public static final double DAILY_FINE = 0.50;
    public static final double MAX_FINE = 20.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private int renewCount = 0;
    private Double fineAmount = 0.0;

    // ───────────────────────────────
    // Utility Methods
    // ───────────────────────────────

    /**
     * Check if the loan is returned.
     * @return true if the loan is returned, false otherwise.
     */
    public boolean isReturned() {
        return returnDate != null;
    }

    /**
     * Check if the loan is overdue.
     * @return true if the loan is overdue, false otherwise.
     */
    public boolean isOverdue() {
        return !isReturned() && dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Check if the loan can be renewed.
     * @return true if the loan can be renewed, false otherwise.
     */
    public boolean canRenew() {
        return renewCount < MAX_RENEWALS && !isOverdue() && !isReturned();
    }

    /**
     * Get the number of days the loan is overdue.
     * @return the number of overdue days.
     */
    public long getOverdueDays() {
        if (dueDate == null || !isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    /**
     * Calculate the current fine for the overdue loan.
     * @return the total fine amount.
     */
    public double calculateCurrentFine() {
        if (!isOverdue()) return 0.0;
        long overdueDays = getOverdueDays();
        double totalFine = overdueDays * DAILY_FINE;
        return Math.min(totalFine, MAX_FINE);
    }

    /**
     * Renews the loan by extending the due date, if allowed.
     * Increments the renew count and throws IllegalStateException if not renewable.
     */
    public void renew() {
        if (!canRenew()) {
            throw new IllegalStateException("Loan cannot be renewed.");
        }
        this.dueDate = this.dueDate.plusDays(LOAN_DURATION_DAYS);  // Extend the due date by 14 days
        this.renewCount += 1;  // Increment the renewal count
    }

    /**
     * Resets the fine amount when the loan is returned.
     */
    public void returnBook() {
        this.fineAmount = 0.0;  // Reset fine upon return
        this.returnDate = LocalDate.now();  // Set return date to current date
    }

    // ───────────────────────────────
    // Getters & Setters
    // ───────────────────────────────

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Member getMember() { return member; }

    public void setMember(Member member) { this.member = member; }

    public Book getBook() { return book; }

    public void setBook(Book book) { this.book = book; }

    public LocalDate getBorrowDate() { return borrowDate; }

    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }

    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public int getRenewCount() { return renewCount; }

    public void setRenewCount(int renewCount) { this.renewCount = renewCount; }

    public Double getFineAmount() { return fineAmount; }

    public void setFineAmount(Double fineAmount) { this.fineAmount = fineAmount; }
}