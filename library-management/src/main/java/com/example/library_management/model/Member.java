package com.example.library_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @PastOrPresent
    private LocalDate registrationDate;

    private boolean active;

    @OneToMany(mappedBy = "member")
    private List<Loan> loans;

    // Getters and setters...
}
