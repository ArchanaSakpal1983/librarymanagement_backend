// Controller Class: For REST API endpoints
package com.example.library_management.controller;

import com.example.library_management.model.Member;

import com.example.library_management.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;

import java.util.List;
import java.util.Optional;

//REST controller for managing members.

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/{id}")
    public Optional<Member> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    @PostMapping
    public Member createMember(@Valid @RequestBody Member member) {
        return memberService.createMember(member);
        
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        member.setId(id); // Ensure ID from path is applied to the object
        return memberService.updateMember(member);
    }
    
    
    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }
}
