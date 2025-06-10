package com.example.library_management.repository;

import com.example.library_management.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// This interface is for Member entity
// Extends JpaRepository to provide built-in CRUD operations and pagination
// @param <Member> The entity type to manage
// @param <Long> The type of the entity's primary key
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find a member by exact username
    Optional<Member> findByUsername(String username);

    // Find all members where the name contains a given string (case-insensitive)
    List<Member> findByNameContainingIgnoreCase(String name);
}