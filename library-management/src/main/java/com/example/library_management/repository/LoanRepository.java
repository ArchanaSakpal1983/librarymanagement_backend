// LoanRepository.java

package com.example.library_management.repository;

import com.example.library_management.model.Loan;
import com.example.library_management.model.Member;
import com.example.library_management.dto.LoanSearchDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Retrieve loans for a specific member
    List<Loan> findByMember(Member member);

    // Find a loan by ID (explicit method for clarity)
    Optional<Loan> findById(Long id);

    // For admin to search loans by member name using a DTO projection
    @Query("SELECT new com.example.library_management.dto.LoanSearchDTO(" +
           "l.id, m.name, b.title, l.borrowDate, l.dueDate, l.returnDate, l.fineAmount) " +
           "FROM Loan l " +
           "JOIN l.member m " +
           "JOIN l.book b " +
           "WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    List<LoanSearchDTO> findByMemberNameContainingIgnoreCase(@Param("namePart") String namePart);

    // Custom query to find all overdue loans for a specific member
    @Query("SELECT l FROM Loan l WHERE l.member = :member AND l.dueDate < CURRENT_DATE AND l.returnDate IS NULL")
    List<Loan> findOverdueLoansForMember(@Param("member") Member member);

    // Optional: Custom query to update the loan's due date directly if needed (though save() can handle this)
    @Modifying
    @Query("UPDATE Loan l SET l.dueDate = :newDueDate WHERE l.id = :loanId")
    int updateLoanDueDate(@Param("loanId") Long loanId, @Param("newDueDate") LocalDate newDueDate);
}