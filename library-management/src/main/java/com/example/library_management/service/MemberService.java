package com.example.library_management.service;

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

    // ─────────────────────────────────────────────
    // 🔐 ADMIN METHODS
    // ─────────────────────────────────────────────

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> getMemberByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }

    public Member createMember(Member member) {
        if (member.getPassword() != null && !member.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        }
        return memberRepository.save(member);
    }

    public Member updateMember(Member updatedMember) {
        Member existing = memberRepository.findById(updatedMember.getId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        existing.setName(updatedMember.getName());
        existing.setEmail(updatedMember.getEmail());
        existing.setUsername(updatedMember.getUsername());
        existing.setActive(updatedMember.isActive());
        existing.setRole(updatedMember.getRole());
        existing.setRegistrationDate(updatedMember.getRegistrationDate());

        if (updatedMember.getPassword() != null && !updatedMember.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedMember.getPassword()));
        }

        return memberRepository.save(existing);
    }

    // Method to delete a member by ID
    public void deleteMember(Long id) {
        // Check if the member exists before attempting deletion
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("Member not found with ID: " + id);
        }
        
        // Delete the member
        memberRepository.deleteById(id);
    }

    public Member renewMembership(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setRegistrationDate(LocalDate.now());
        return memberRepository.save(member);  // save the updated member
    }

    // ─────────────────────────────────────────────
    // 🙋 SELF-SERVICE (Admin or Member)
    // ─────────────────────────────────────────────

    public Member getCurrentAuthenticatedMember() {
        String username = getCurrentUsername();
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated member not found"));
    }

    public Member updateOwnProfile(Member updatedInfo) {
        Member current = getCurrentAuthenticatedMember();

        current.setName(updatedInfo.getName());
        current.setEmail(updatedInfo.getEmail());
        current.setUsername(updatedInfo.getUsername());
        current.setActive(updatedInfo.isActive());

        if (updatedInfo.getPassword() != null && !updatedInfo.getPassword().isBlank()) {
            current.setPassword(passwordEncoder.encode(updatedInfo.getPassword()));
        }

        return memberRepository.save(current);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }

    // ─────────────────────────────────────────────
    // 📚 LOAN UTILITY METHODS
    // ─────────────────────────────────────────────

    public boolean isMembershipValid(Member member) {
        LocalDate expiry = member.getRegistrationDate().plusYears(1);
        return LocalDate.now().isBefore(expiry);
    }

    public boolean hasOverdueBooks(Member member) {
        return member.getLoans().stream()
                .anyMatch(loan -> loan.getReturnDate() == null && loan.getDueDate().isBefore(LocalDate.now()));
    }

    public long getActiveLoanCount(Member member) {
        return member.getLoans().stream()
                .filter(loan -> loan.getReturnDate() == null)
                .count();
    }
}