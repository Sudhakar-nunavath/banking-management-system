
package com.sudhakar.banking.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final CustomUserDetailsService users;

    public JwtAuthFilter(JwtService jwt, CustomUserDetailsService users) {
        this.jwt = jwt;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain
    ) throws ServletException, IOException {

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwt.isValid(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    String email = jwt.extractUsername(token);
                    UserDetails d = users.loadUserByUsername(email);

                    var auth = new UsernamePasswordAuthenticationToken(
                            d, null, d.getAuthorities()
                    );

                    auth.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(req)
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    System.out.println("JWT USER: " + email);
                    System.out.println("JWT AUTHORITIES: " + d.getAuthorities());

                } catch (Exception e) {
                    System.err.println("JWT AUTH ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (!jwt.isValid(token)) {
                System.err.println("JWT AUTH ERROR: Invalid or expired token");
            }
        }

        chain.doFilter(req, res);
    }
}
