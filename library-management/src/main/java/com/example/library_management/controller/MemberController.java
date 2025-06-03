package com.example.library_management.controller;

import com.example.library_management.model.Member;
import com.example.library_management.service.MemberService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // ───────────────────────────────
    // ADMIN ENDPOINTS
    // ───────────────────────────────

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Member>> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        return ResponseEntity.ok(memberService.createMember(member));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        member.setId(id);
        return ResponseEntity.ok(memberService.updateMember(member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    // ───────────────────────────────
    // MEMBER-SELF ENDPOINTS (JWT protected)
    // ───────────────────────────────

    /**
     * Authenticated member retrieves their own details
     */
    @GetMapping("/me")
    public ResponseEntity<Member> getCurrentMember() {
        return ResponseEntity.ok(memberService.getCurrentAuthenticatedMember());
    }

    /**
     * Authenticated member updates their own profile
     */
    @PutMapping("/me")
    public ResponseEntity<Member> updateMyProfile(@Valid @RequestBody Member updatedMember) {
        return ResponseEntity.ok(memberService.updateOwnProfile(updatedMember));
    }
}