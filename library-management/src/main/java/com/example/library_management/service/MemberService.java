package com.example.library_management.service;

import com.example.library_management.model.Loan;
import com.example.library_management.model.Member;
import com.example.library_management.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ───────────────────────────────────────────────────────
    // ADMIN USE – Unrestricted access (all members)
    // ───────────────────────────────────────────────────────

    public Optional<Member> getMemberByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public Member updateMember(Member member) {
        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    // ───────────────────────────────────────────────────────
    // MEMBER USE – Self-service using JWT-authenticated user
    // ───────────────────────────────────────────────────────

    public Member getCurrentAuthenticatedMember() {
        String username = getCurrentUsername();
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated member not found"));
    }

    public Member updateOwnProfile(Member updatedInfo) {
        Member currentMember = getCurrentAuthenticatedMember();

        currentMember.setName(updatedInfo.getName());
        currentMember.setEmail(updatedInfo.getEmail());
        currentMember.setUsername(updatedInfo.getUsername());
        currentMember.setActive(updatedInfo.isActive());

        if (updatedInfo.getPassword() != null && !updatedInfo.getPassword().isBlank()) {
            String encoded = passwordEncoder.encode(updatedInfo.getPassword());
            currentMember.setPassword(encoded);
        }

        return memberRepository.save(currentMember);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            return principal.toString();
        }
    }

    // ───────────────────────────────────────────────────────
    // Business Logic Helpers
    // ───────────────────────────────────────────────────────

    public boolean isMembershipValid(Member member) {
        LocalDate expiryDate = member.getRegistrationDate().plusYears(1);
        return LocalDate.now().isBefore(expiryDate);
    }

    public boolean hasOverdueBooks(Member member) {
        return member.getLoans().stream().anyMatch(loan ->
            loan.getReturnDate() == null && loan.getDueDate().isBefore(LocalDate.now())
        );
    }

    public long getActiveLoanCount(Member member) {
        return member.getLoans().stream()
            .filter(loan -> loan.getReturnDate() == null)
            .count();
    }
}
