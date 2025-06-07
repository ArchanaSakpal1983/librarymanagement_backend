package com.example.library_management.repository;

import com.example.library_management.model.Loan;
import com.example.library_management.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// This interface handles data access for Loan entity
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    // Fetch all loans for a specific member
    List<Loan> findByMember(Member member);
}