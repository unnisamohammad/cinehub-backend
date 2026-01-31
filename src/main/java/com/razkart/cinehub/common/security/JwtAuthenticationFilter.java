package com.razkart.cinehub.common.security;

import com.razkart.cinehub.common.util.JwtUtil;
import com.razkart.cinehub.user.entity.User;
import com.razkart.cinehub.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = extractJwt(request);
            if (StringUtils.hasText(jwt) && jwtUtil.isTokenValid(jwt)) {
                authenticateUser(jwt, request);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String jwt, HttpServletRequest request) {
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null && user.isActive()) {
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + jwtUtil.extractRole(jwt)));
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated user: {}", email);
        }
    }

    private String extractJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX))
                ? bearerToken.substring(BEARER_PREFIX.length()) : null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/") || path.startsWith("/api/swagger-ui") ||
               path.startsWith("/api/api-docs") || path.startsWith("/api/v3/api-docs") ||
               path.startsWith("/api/webjars/") || path.equals("/api/actuator/health");
    }
}
