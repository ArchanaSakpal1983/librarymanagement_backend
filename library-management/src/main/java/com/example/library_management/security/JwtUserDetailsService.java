package com.example.library_management.security;

import com.example.library_management.model.Member;
import com.example.library_management.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        
        if (member.getRole() == null || member.getRole().isEmpty()) {
            throw new IllegalStateException("User has no roles assigned");
        }

        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword()) // Must be BCrypt encoded
                .authorities(mapRolesToAuthorities(member.getRole()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private List<GrantedAuthority> mapRolesToAuthorities(String role) {
        // If you have multiple roles in future, split them here
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }
}
