// LoanSearchDTO.java
// Implemented for security between the AdminDashboard & backend

// This Data Transfer Object (DTO) used to transfer data 
// between layers of an application (backend to frontend) 
// without exposing the entire member entity
// It will return with only the required object we requested.




package com.example.library_management.dto;


import java.time.LocalDate;

public class LoanSearchDTO {
	// Member search associated by id
	private Long id;

    // Member name associated with the loan
    private String memberName;

    // Book title associated with the loan
    private String bookTitle;

    // Date the book was borrowed
    private LocalDate borrowDate;

    // Date the book is due to be returned
    private LocalDate dueDate;

    // Date the book was actually returned (if any)
    private LocalDate returnDate;

    // Fine amount (if any) calculated for the loan
    private double fineAmount;

    // Required constructor for JPQL query result mapping
    public LoanSearchDTO(Long id, String memberName, String bookTitle,
                         LocalDate borrowDate, LocalDate dueDate,
                         LocalDate returnDate, Double fineAmount) {
        this.id = id;
        this.memberName = memberName;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }

    // Getter
    public Long getId() { return id; }
    public String getMemberName() { return memberName; }
    public String getBookTitle() { return bookTitle; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public Double getFineAmount() { return fineAmount; }
}