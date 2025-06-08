package com.example.library_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    private String password;

    @Email
    @NotBlank
    private String email;

    @PastOrPresent
    private LocalDate registrationDate;

    private boolean active = true;

    private String role; // e.g. "member", "admin"

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Loan> loans = new ArrayList<>();

    // --- Constructors ---
    public Member() {}

    // --- Lifecycle callback ---
    @PrePersist
    public void prePersist() {
        if (this.registrationDate == null) {
            this.registrationDate = LocalDate.now();
        }
    }

    // --- Business Logic Helpers ---

    // Membership is valid for 1 year from registration
    public boolean isMembershipValid() {
        return registrationDate != null && LocalDate.now().isBefore(registrationDate.plusYears(1));
    }

    // Check if the member has any overdue loans
    public boolean hasOverdueLoans() {
        return loans.stream()
                .anyMatch(loan -> loan.isOverdue() && !loan.isReturned());
    }

    // Count currently borrowed books
    public long getCurrentBorrowedBooksCount() {
        return loans.stream()
                .filter(loan -> !loan.isReturned())
                .count();
    }

    // Optional: calculate total fine (or you could move this to a service)
    public double getTotalOutstandingFines() {
        return loans.stream()
                .mapToDouble(Loan::calculateCurrentFine)
                .sum();
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<Loan> getLoans() { return loans; }
    public void setLoans(List<Loan> loans) { this.loans = loans; }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
