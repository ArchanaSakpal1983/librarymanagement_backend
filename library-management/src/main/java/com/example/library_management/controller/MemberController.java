package com.example.library_management.controller;

import com.example.library_management.model.Loan;
import com.example.library_management.model.Member;
import com.example.library_management.service.MemberService;
import jakarta.validation.Valid;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Import SecurityContextHolder
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:5173") // Allow frontend access during development
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class); // Initialize Logger

    @Autowired
    private MemberService memberService;

    // ───────────────────────────────────────
    // 🔐 ADMIN ENDPOINTS (Role: ADMIN only)
    // ───────────────────────────────────────

    /**
     * Retrieves all members. Only accessible by users with 'ADMIN' role.
     * @return A list of all members.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        logger.info("ADMIN: Attempting to fetch all members.");
        List<Member> members = memberService.getAllMembers();
        logger.info("ADMIN: Fetched {} members.", members.size());
        return ResponseEntity.ok(members);
    }

    /**
     * Searches members by name. Only accessible by users with 'ADMIN' role.
     * @param name The name to search for.
     * @return A list of members matching the name.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchMembersByName(@RequestParam String name) {
        logger.info("ADMIN: Searching members by name: {}", name);
        List<Member> members = memberService.searchMembersByName(name);
        logger.info("ADMIN: Found {} members matching name: {}", members.size(), name);
        return ResponseEntity.ok(members);
    }

    /**
     * Retrieves a member by their ID. Only accessible by users with 'ADMIN' role.
     * @param id The ID of the member.
     * @return ResponseEntity containing the Member if found.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        logger.info("ADMIN: Attempting to fetch member with ID: {}", id);
        return memberService.getMemberById(id)
                .map(m -> {
                    logger.info("ADMIN: Found member with ID {}: {}", id, m.getUsername());
                    return ResponseEntity.ok(m);
                })
                .orElseGet(() -> {
                    logger.warn("ADMIN: Member with ID {} not found.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Retrieves a member by their username. Only accessible by users with 'ADMIN' role.
     * @param username The username of the member.
     * @return ResponseEntity containing the Member if found.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity<Member> getMemberByUsername(@PathVariable String username) {
        logger.info("ADMIN: Attempting to fetch member by username: {}", username);
        return memberService.getMemberByUsername(username)
                .map(m -> {
                    logger.info("ADMIN: Found member with username {}: {}", username, m.getUsername());
                    return ResponseEntity.ok(m);
                })
                .orElseGet(() -> {
                    logger.warn("ADMIN: Member with username {} not found.", username);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Creates a new member. Only accessible by users with 'ADMIN' role.
     * @param member The member object to create.
     * @return ResponseEntity containing the created Member.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        // Log details of the authenticated user at the start of the method
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("createMember: Authenticated user: {}", authentication.getName());
            logger.info("createMember: User Authorities: {}", authentication.getAuthorities()); // CRITICAL LOG
        } else {
            logger.warn("createMember: Unauthenticated access attempt to create member.");
        }

        // The actual business logic to create the member
        Member createdMember = memberService.createMember(member);
        logger.info("ADMIN: Member created successfully: {}", createdMember.getUsername());
        return ResponseEntity.ok(createdMember);
    }

    /**
     * Updates an existing member. Only accessible by users with 'ADMIN' role.
     * @param id The ID of the member to update.
     * @param member The updated member details.
     * @return ResponseEntity containing the updated Member.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        logger.info("ADMIN: Attempting to update member with ID: {}", id);
        member.setId(id); // ensure ID consistency
        Member updatedMember = memberService.updateMember(member);
        logger.info("ADMIN: Member with ID {} updated successfully.", id);
        return ResponseEntity.ok(updatedMember);
    }

    /**
     * Deletes a member by their ID. Only accessible by users with 'ADMIN' role.
     * @param id The ID of the member to delete.
     * @return ResponseEntity indicating success or failure.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        logger.info("ADMIN: Attempting to delete member with ID: {}", id);
        try {
            memberService.deleteMember(id);
            logger.info("ADMIN: Member with ID {} deleted successfully.", id);
            return ResponseEntity.noContent().build(); // 204 No Content on successful deletion
        } catch (RuntimeException e) {
            logger.error("ADMIN: Error deleting member with ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build(); // 404 Not Found if the member does not exist
        }
    }

    /**
     * Renews a member's membership. Only accessible by users with 'ADMIN' role.
     * @param id The ID of the member to renew.
     * @return ResponseEntity containing the renewed Member.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/renew")
    public ResponseEntity<Member> renewMembership(@PathVariable Long id) {
        logger.info("ADMIN: Attempting to renew membership for member with ID: {}", id);
        Member renewed = memberService.renewMembership(id);
        logger.info("ADMIN: Membership renewed for member with ID: {}", id);
        return ResponseEntity.ok(renewed);
    }

    // ─────────────────────────────────────────────
    // 🙋 MEMBER SELF-SERVICE (Role: ADMIN or MEMBER)
    // ─────────────────────────────────────────────

    /**
     * Retrieves the profile of the currently authenticated member.
     * Accessible by users with 'MEMBER' or 'ADMIN' role.
     * This method leverages the service to get the current authenticated user's details.
     * @return ResponseEntity containing the Member's profile.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping("/me")
    public ResponseEntity<Member> getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("SELF-SERVICE: Fetching profile for authenticated user: {}", authentication != null ? authentication.getName() : "N/A");
        
        // This method should retrieve the member based on the authenticated user from the SecurityContext
        Member currentMember = memberService.getCurrentAuthenticatedMember();
        logger.info("SELF-SERVICE: Successfully fetched profile for user: {}", currentMember.getUsername());
        return ResponseEntity.ok(currentMember);
    }

    /**
     * Updates the profile of the currently authenticated member.
     * Accessible by users with 'MEMBER' or 'ADMIN' role.
     * @param updatedMember The updated member details.
     * @return ResponseEntity containing the updated Member.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @PutMapping("/me")
    public ResponseEntity<Member> updateMyProfile(@Valid @RequestBody Member updatedMember) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("SELF-SERVICE: Attempting to update profile for authenticated user: {}", authentication != null ? authentication.getName() : "N/A");
        
        // This method should update the currently authenticated user's profile
        Member updated = memberService.updateOwnProfile(updatedMember);
        logger.info("SELF-SERVICE: Profile updated successfully for user: {}", updated.getUsername());
        return ResponseEntity.ok(updated);
    }

    /**
     * Retrieves loans for the currently authenticated member.
     * Accessible by users with 'MEMBER' or 'ADMIN' role.
     * This method retrieves loans associated with the currently authenticated member.
     * @return ResponseEntity containing a list of loans.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping("/me/loans")
    public ResponseEntity<List<Loan>> getCurrentMemberLoans() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("SELF-SERVICE: Fetching loans for authenticated user: {}", authentication != null ? authentication.getName() : "N/A");
        
        // This method should retrieve the current authenticated member's loans
        Member current = memberService.getCurrentAuthenticatedMember(); // Use existing service method
        List<Loan> loans = current.getLoans(); // Assuming Member model has getLoans()
        
        logger.info("SELF-SERVICE: Found {} loans for user: {}", loans.size(), current.getUsername());
        return ResponseEntity.ok(loans);
    }
}