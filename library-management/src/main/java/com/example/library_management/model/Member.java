package com.example.library_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // Keep import for clarity if other fields might use it, but not for password
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Member in the Library Management System.
 * This entity stores all details related to a library member,
 * including personal information, authentication credentials,
 * registration details, and a list of their associated loans.
 *
 * It uses JPA annotations for ORM mapping to a database table.
 */
@Entity // Marks this class as a JPA entity, mapped to a database table
@Table(name = "member") // Specifies the table name if it differs from the class name (optional, but good practice)
public class Member {

    /**
     * Unique identifier for the member.
     * Generated automatically by the database.
     */
    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures ID generation using database identity column
    private Long id;

    /**
     * The full name of the member.
     * Cannot be blank.
     */
    @NotBlank(message = "Name cannot be blank") // Ensures the string is not null and contains at least one non-whitespace character
    private String name;

    /**
     * The unique username used for login.
     * Must be unique across all members and cannot be null.
     */
    @Column(unique = true, nullable = false, length = 50) // Ensures uniqueness and non-nullability at the database level, defines max length
    @NotBlank(message = "Username cannot be blank") // Frontend validation
    private String username;

    /**
     * The member's password.
     * NOTE: This field will store the BCrypted (hashed) password in the database.
     * When receiving new member data from the frontend, this field will accept the plain text password,
     * which is then hashed by the `PasswordEncoder` in the `MemberService` before persistence.
     *
     * IMPORTANT FIX: @JsonIgnore has been removed. This allows the password field
     * to be properly deserialized (read) from incoming JSON requests (e.g., from AddMemberForm).
     * Without this fix, the password sent by the frontend would be ignored, leading to a null password
     * being saved to the database and a 'Column cannot be null' error.
     *
     * CONCERN: If this entity is used directly for API responses, the encoded password will be exposed.
     * For production, it's recommended to use Data Transfer Objects (DTOs) for API input/output
     * to control which fields are exposed.
     */
    @NotBlank(message = "Password cannot be blank") // Ensures the string is not null and contains at least one non-whitespace character
    @Column(nullable = false, length = 100) // Ensures non-nullability at DB level, provides length for encoded hash
    private String password;

    /**
     * The member's email address.
     * Must be a valid email format and cannot be blank.
     */
    @Email(message = "Email must be a valid email address") // Validates email format
    @NotBlank(message = "Email cannot be blank") // Ensures the string is not null and contains at least one non-whitespace character
    @Column(nullable = false, unique = true) // Email should also be unique in the DB
    private String email;

    /**
     * The date the member registered.
     * Automatically set to the current date if not provided, before persistence.
     */
    @PastOrPresent(message = "Registration date cannot be in the future") // Ensures date is not in the future
    @Column(nullable = false) // Ensure registration date is always present in DB
    private LocalDate registrationDate;

    /**
     * Indicates if the member's account is active.
     * Defaulted to true upon creation.
     */
    @Column(nullable = false) // Ensures active status is always present
    private boolean active = true;

    /**
     * The role of the member within the system (e.g., "member", "admin").
     * Used for authorization. Stored as a simple string.
     */
    @NotBlank(message = "Role cannot be blank") // Role must be provided
    @Column(nullable = false, length = 20) // Defines role column properties
    private String role; // e.g. "member", "admin"

    /**
     * A list of loans associated with this member.
     * 'mappedBy' indicates that the 'member' field in the Loan entity owns the relationship.
     * 'cascade = CascadeType.ALL' means all operations (persist, merge, remove, refresh, detach)
     * performed on the Member entity will cascade to its associated Loan entities.
     * 'fetch = FetchType.LAZY' means loans are loaded only when explicitly accessed.
     * `JsonManagedReference` helps to manage bidirectional relationships, preventing infinite recursion
     * during JSON serialization by marking this side as the "managing" side.
     */
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference // Part of Jackson's bidirectional relationship handling
    private List<Loan> loans = new ArrayList<>();

    // --- Constructors ---

    /**
     * Default constructor required by JPA.
     */
    public Member() {}

    /**
     * Constructor for creating a new Member with essential details.
     *
     * @param name The full name of the member.
     * @param username The unique username.
     * @param password The plain text password (will be encoded before saving).
     * @param email The member's email address.
     * @param role The role of the member (e.g., "member", "admin").
     */
    public Member(String name, String username, String password, String email, String role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.registrationDate = LocalDate.now(); // Set default registration date
        this.active = true; // Set default active status
    }


    // --- Lifecycle callback ---

    /**
     * Method annotated with @PrePersist runs before a new entity is persisted (saved for the first time).
     * This ensures the registrationDate is set automatically if not already provided.
     */
    @PrePersist
    public void prePersist() {
        if (this.registrationDate == null) {
            this.registrationDate = LocalDate.now();
        }
    }

    // --- Business Logic Helpers ---
    // These methods provide utility for business rules related to a Member.

    /**
     * Checks if the member's registration is still valid.
     * Membership is considered valid for 1 year from the registration date.
     *
     * @return true if the membership is valid, false otherwise.
     */
    public boolean isMembershipValid() {
        return registrationDate != null && LocalDate.now().isBefore(registrationDate.plusYears(1));
    }

    /**
     * Checks if the member has any overdue loans that have not yet been returned.
     *
     * @return true if there is at least one overdue and unreturned loan, false otherwise.
     */
    public boolean hasOverdueLoans() {
        return loans.stream()
                .anyMatch(loan -> loan.isOverdue() && !loan.isReturned());
    }

    /**
     * Counts the number of books currently borrowed by the member (i.e., not yet returned).
     *
     * @return The count of active loans.
     */
    public long getCurrentBorrowedBooksCount() {
        return loans.stream()
                .filter(loan -> !loan.isReturned())
                .count();
    }

    /**
     * Calculates the total outstanding fines for all loans associated with this member.
     *
     * @return The sum of current fines.
     */
    public double getTotalOutstandingFines() {
        return loans.stream()
                .mapToDouble(Loan::calculateCurrentFine)
                .sum();
    }

    // --- Getters and Setters ---
    // Standard getter and setter methods for all fields.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    // --- toString Method ---

    /**
     * Provides a string representation of the Member object, useful for logging and debugging.
     * Note: Password is intentionally excluded from toString for security reasons.
     * @return A string representation of the Member.
     */
    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                // Loans are typically lazy-loaded and not included here to avoid recursion/performance issues
                '}';
    }
}

