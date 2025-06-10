package com.example.library_management.security;

import com.example.library_management.model.Member;
import com.example.library_management.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Autowired
    public JwtUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (member.getRole() == null || member.getRole().isBlank()) {
            throw new IllegalStateException("User has no roles assigned");
        }

        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword()) // BCrypt password
                .authorities(getAuthorities(member.getRole()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private List<GrantedAuthority> getAuthorities(String role) {
        // Expandable to multiple roles in future (e.g., comma-separated)
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }
}