package com.example.library_management.service;

import com.example.library_management.model.Member;
import com.example.library_management.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    // ───────────────────────────────────────────────────────
    // ADMIN USE – Unrestricted access (all members)
    // ───────────────────────────────────────────────────────

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

    /**
     * Gets the current authenticated member based on JWT token
     */
    public Member getCurrentAuthenticatedMember() {
        String username = getCurrentUsername();
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated member not found"));
    }

    /**
     * Updates the authenticated member's profile (excluding role or username)
     */
    public Member updateOwnProfile(Member updatedInfo) {
        Member currentMember = getCurrentAuthenticatedMember();
        currentMember.setName(updatedInfo.getName());
        currentMember.setEmail(updatedInfo.getEmail());
        currentMember.setActive(updatedInfo.isActive());
        // Optionally: handle password changes separately
        return memberRepository.save(currentMember);
    }

    /**
     * Helper method to get current username from JWT context
     */
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            return principal.toString();
        }
    }
}