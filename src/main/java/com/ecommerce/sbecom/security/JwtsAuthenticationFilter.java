package com.ecommerce.sbecom.security;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//@Component
//@RequiredArgsConstructor
@Slf4j
public class JwtsAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    public JwtsAuthenticationFilter(
            JwtService jwtService,
           UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        log.info("Filter called for: {}", request.getRequestURI());
        // CRITICAL: Skip if already authenticated (prevents duplicate calls)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }


        // Try cookie FIRST
        String token = null;
        String authMethod = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("Cookie: {} = {}", cookie.getName(), cookie.getValue().substring(0, Math.min(20, cookie.getValue().length())));
                if ("access-token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    authMethod = "COOKIE";
                    break;
                }
            }
        }

// If no cookie, try header
        if (token == null) {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
                authMethod = "HEADER";
            }
        }

// If no token found, skip authentication
        if (token == null) {
            log.debug("No JWT token found in cookie or header");
            log.info("Access token missing. Trying refresh fallback...");
            String refreshToken = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refresh-token".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
            if (refreshToken != null) {
                try {
                    if (jwtService.isRefreshToken(refreshToken)) {

                        UUID userId = jwtService.getUserIdFromToken(refreshToken);

                        User user = userRepository.findByIdWithRoles(userId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                        if (!user.isEnabled()) {
                            filterChain.doFilter(request, response);
                            return;
                        }

                        // 🔥 create new access token
                        String newAccess = jwtService.generateToken(user);

                        Cookie newAccessCookie = new Cookie("access-token", newAccess);
                        newAccessCookie.setHttpOnly(true);
                        newAccessCookie.setPath("/");
                        response.addCookie(newAccessCookie);

                        // 🔥 VERY IMPORTANT → set authentication
                        List<GrantedAuthority> authorities = user.getRoles() != null
                                ? user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                .collect(Collectors.toList())
                                : Collections.emptyList();

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user, null, authorities);

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.info("User authenticated via refresh fallback: {}", user.getEmail());
                    }

                } catch (Exception e) {
                    log.warn("Refresh fallback failed: {}", e.getMessage());
                }
            }

            filterChain.doFilter(request, response);
            return;
        }
        log.info("JWT token extracted from: {}", authMethod);

        try {


            // Validate token (this also parses it)
            if (!jwtService.validateToken(token)) {
                log.warn("Invalid JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            // Check if it's an access token (not refresh token)
            if (!jwtService.isAccessToken(token)) {
                log.warn("Refresh token used in place of access token");
                filterChain.doFilter(request, response);
                return;
            }

            // Extract user ID from token
            UUID userId = jwtService.getUserIdFromToken(token);
            List<String> rolesFromToken = jwtService.getRolesFromToken(token);
            log.debug("Extracted user ID from token: {}", userId);

            // Load user from database
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> {
//                        log.error("User not found with ID: {}", userId);
//                        return new UsernameNotFoundException("User not found with ID: " + userId);
//                    });
            User user = userRepository.findByIdWithRoles(userId)
                    .orElseThrow(() -> {
                        log.error("User not found with ID: {}", userId);
                        return new UsernameNotFoundException("User not found with ID: " + userId);
                    });

            // Check if user is enabled
            if (!user.isEnabled()) {
                log.warn("User account is disabled: {}", user.getEmail());
                filterChain.doFilter(request, response);
                return;
            }

            // Extract authorities from user roles
            List<GrantedAuthority> authorities = user.getRoles() != null && !user.getRoles().isEmpty()
                    ? user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList())
                    : Collections.emptyList();

            log.debug("User roles: {}", authorities);

            // Create authentication token
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities
                    );

            // Set additional details
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authentication set successfully for user: {}", user.getEmail());

            log.debug("Authentication set successfully for user: {}", user.getEmail());

        } catch (ExpiredJwtException e) {
            log.error("JWT token has expired: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        // Always continue the filter chain
        filterChain.doFilter(request, response);
    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        // Skip filter for public endpoints (optional optimization)
//        String path = request.getRequestURI();
//        return path.startsWith("/api/v1/auth/signin") ||
//                path.startsWith("/api/v1/auth/signup") ||
//                path.startsWith("/api/v1/auth/register")||
//                path.startsWith("/api/v1/auth/refreshtoken")
//                ;
//    }
@Override
protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    log.info(">>> shouldNotFilter checking path: '{}'", path);

    boolean skip = path.startsWith("/api/v1/auth/signin") ||
            path.startsWith("/api/v1/auth/signup") ||
            path.startsWith("/api/v1/auth/register") ||
            path.startsWith("/api/v1/auth/refreshtoken");

    log.info(">>> shouldNotFilter result: {} for path: '{}'", skip, path);
    return skip;
}
}