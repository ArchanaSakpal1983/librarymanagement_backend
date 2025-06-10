package com.example.library_management.controller;

import com.example.library_management.model.Loan;
import com.example.library_management.model.Member;
import com.example.library_management.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:5173")  // Allow frontend access during development
public class MemberController {

    @Autowired
    private MemberService memberService;

    // ───────────────────────────────────────
    // 🔐 ADMIN ENDPOINTS (Role: ADMIN only)
    // ───────────────────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchMembersByName(@RequestParam String name) {
        return ResponseEntity.ok(memberService.searchMembersByName(name));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity<Member> getMemberByUsername(@PathVariable String username) {
        return memberService.getMemberByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        return ResponseEntity.ok(memberService.createMember(member));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        member.setId(id);  // ensure ID consistency
        return ResponseEntity.ok(memberService.updateMember(member));
    }

    // ─────────────────────────────────────────────
    // 🔥 DELETE METHOD: ADMIN ONLY
    // ─────────────────────────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.noContent().build();  // 204 No Content on successful deletion
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();  // 404 Not Found if the member does not exist
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/renew")
    public ResponseEntity<Member> renewMembership(@PathVariable Long id) {
        Member renewed = memberService.renewMembership(id);
        return ResponseEntity.ok(renewed);
    }
    

    // ─────────────────────────────────────────────
    // 🙋 MEMBER SELF-SERVICE (Role: ADMIN or MEMBER)
    // ─────────────────────────────────────────────

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping("/me")
    public ResponseEntity<Member> getCurrentMember() {
        return ResponseEntity.ok(memberService.getCurrentAuthenticatedMember());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @PutMapping("/me")
    public ResponseEntity<Member> updateMyProfile(@Valid @RequestBody Member updatedMember) {
        return ResponseEntity.ok(memberService.updateOwnProfile(updatedMember));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping("/me/loans")
    public ResponseEntity<List<Loan>> getCurrentMemberLoans() {
        Member current = memberService.getCurrentAuthenticatedMember();
        return ResponseEntity.ok(current.getLoans());
    }
}