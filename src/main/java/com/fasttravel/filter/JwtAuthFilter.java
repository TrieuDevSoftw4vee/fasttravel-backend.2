package com.fasttravel.filter;

import com.fasttravel.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwt;

    protected void doFilterInternal(HttpServletRequest q, HttpServletResponse p, FilterChain c) throws ServletException, IOException {
        String h = q.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) try {
            Claims x = jwt.parse(h.substring(7));
            var a = new UsernamePasswordAuthenticationToken(x.getSubject(), null, List.of(new SimpleGrantedAuthority("ROLE_" + x.get("role", String.class))));
            a.setDetails(((Number) x.get("uid")).longValue());
            SecurityContextHolder.getContext().setAuthentication(a);
        } catch (Exception ignored) {
        }
        c.doFilter(q, p);
    }
}
