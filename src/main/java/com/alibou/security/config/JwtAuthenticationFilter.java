package com.alibou.security.config;

import com.alibou.security.service.JwtService;
import com.alibou.security.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;

  // Danh sách các endpoint công khai
  private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
          "/api/v1/auth/register",
          "/api/v1/auth/authenticate",
          "/api/v1/auth/refresh-token",
          "/api/v1/auth/send-verification",
          "/api/v1/auth/verifyCode"
  );

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String requestPath = request.getServletPath();
    logger.debug("Processing request to: {}", requestPath);

    if (PUBLIC_ENDPOINTS.contains(requestPath)) {
      logger.debug("Request is to a public endpoint, skipping JWT authentication");
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader("Authorization");
    logger.debug("Authorization header: {}", authHeader);

    final String jwt;
    final String userEmail;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      logger.warn("Authorization header missing or does not start with Bearer");
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7);
    logger.debug("Extracted JWT: {}", jwt);

    try {
      userEmail = jwtService.extractUsername(jwt);
      logger.debug("Extracted userEmail from JWT: {}", userEmail);
    } catch (Exception e) {
      logger.error("Failed to extract username from JWT", e);
      filterChain.doFilter(request, response);
      return;
    }

    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails;
      try {
        userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        logger.debug("Loaded user details for user: {}", userEmail);
      } catch (UsernameNotFoundException e) {
        logger.error("User not found: {}", userEmail, e);
        filterChain.doFilter(request, response);
        return;
      }

      boolean isTokenValid = tokenRepository.findByToken(jwt)
              .map(t -> !t.isExpired() && !t.isRevoked())
              .orElse(false);

      logger.debug("Is token valid: {}", isTokenValid);

      if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
        logger.debug("JWT is valid, setting authentication");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      } else {
        logger.warn("JWT token is invalid or expired");
      }
    } else {
      logger.warn("UserEmail is null or already authenticated");
    }

    filterChain.doFilter(request, response);
  }
}
