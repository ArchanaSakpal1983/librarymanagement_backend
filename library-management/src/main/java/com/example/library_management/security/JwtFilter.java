package com.example.library_management.security;

import com.example.library_management.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final JwtUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, JwtUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        logger.info("Processing request for URI: {}", request.getRequestURI());

        // Skip JWT validation for CORS preflight (OPTIONS requests)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.info("Skipping JWT filter for OPTIONS request.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token from header if present and starts with "Bearer "
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7); // Remove "Bearer " prefix
                logger.info("Extracted JWT: {}", jwt.substring(0, Math.min(jwt.length(), 30)) + "..."); // Log first 30 chars
                username = jwtUtil.extractUsername(jwt); // Decode and extract username from token
                logger.info("Extracted username from JWT: {}", username);
            } else {
                logger.info("No Authorization header or not starting with 'Bearer '.");
            }

            // Only proceed if the token is present and the user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.info("Attempting to load UserDetails for username: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate token against username and ensure it's not expired
                logger.info("Validating token for user: {}", username);
                if (jwtUtil.validateToken(jwt, username)) {
                    logger.info("Token IS VALID for user: {}", username);
                    logger.info("User Details Authorities from UserDetailsService: {}", userDetails.getAuthorities()); // CRITICAL LOG

                    // Set Spring Security Authentication object if valid
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("SecurityContextHolder populated for user: {}", username);
                    logger.info("SecurityContextHolder Authorities: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities()); // CRITICAL LOG
                } else {
                    logger.warn("JWT token IS INVALID for user: {}", username);
                }
            } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.info("User already authenticated in SecurityContextHolder: {}", SecurityContextHolder.getContext().getAuthentication().getName());
                logger.info("Current SecurityContextHolder Authorities: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities()); // CRITICAL LOG
            } else {
                logger.info("No valid username from token or user already authenticated for current request.");
            }
        } catch (Exception e) {
            // Log any JWT-related errors for troubleshooting
            logger.error("JWT validation error: {} - {}. Request URI: {}", e.getClass().getSimpleName(), e.getMessage(), request.getRequestURI(), e);
        }

        // Continue filter chain regardless of authentication result
        filterChain.doFilter(request, response);
    }
}