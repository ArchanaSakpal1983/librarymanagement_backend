package com.example.library_management.repository;

import com.example.library_management.model.Loan;
import com.example.library_management.model.Member;
import com.example.library_management.dto.LoanSearchDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // For member loan viewing
    List<Loan> findByMember(Member member);

    // For admin search by member name (using DTO projection)
    // use of @Query for JPQL to join the loan, member and book entities and perform case insensitive searches

    @Query("SELECT new com.example.library_management.dto.LoanSearchDTO(" +
           "l.id, m.name, b.title, l.borrowDate, l.dueDate, l.returnDate, l.fineAmount) " +
           "FROM Loan l " +
           "JOIN l.member m " +
           "JOIN l.book b " +
           "WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    List<LoanSearchDTO> findByMemberNameContainingIgnoreCase(@Param("namePart") String namePart);
}