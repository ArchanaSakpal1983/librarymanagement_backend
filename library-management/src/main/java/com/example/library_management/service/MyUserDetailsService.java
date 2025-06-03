// USED ONLY FOR TESTING. DEPRECATED.
// ================================
// MyUserDetailsService.java
// ================================
//package com.example.library_management.service;

//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.beans.factory.annotation.Value;


//import java.util.Collections;

//@Service
//public class MyUserDetailsService implements UserDetailsService {

//    @Value("${app.admin.username}")
//    private String adminUsername;
//    @Value("${app.admin.password}")
//    private String adminPasswordHash;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        if (adminUsername.equals(username)) {
//            return User.withUsername(adminUsername)
//                    .password(adminPasswordHash)
//                    .roles("USER")
//                    .build();
//        } else {
//            throw new UsernameNotFoundException("User not found");
//        }
//    }
//}