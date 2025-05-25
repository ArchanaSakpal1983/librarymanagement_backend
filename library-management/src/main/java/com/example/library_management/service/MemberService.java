package com.example.library_management.service;

import com.example.library_management.model.Member;
import com.example.library_management.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Service layer for handling business logic related to Member

@Service		// Marks this class as a service bean
public class MemberService {

    @Autowired  // injects the MemberRepository dependency
    //Assign class MemberRepository to field name memberRepository
    private MemberRepository memberRepository;
    
    // fetch all member from DB
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
    // fetch member by member ID
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }
    // create new member
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }
    // delete member by ID
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
