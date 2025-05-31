package com.app.playbooker.filter;

import com.app.playbooker.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = jwtUtil.getJWTToken(request);
        if(!StringUtils.hasText(jwt)) {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7);
        }

        if (!jwtUtil.validateToken(jwt)) {
            log.error("JWT token is expired!!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtUtil.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims = jwtUtil.extractAllClaims(jwt);
            List<String> roles = (List<String>) claims.get("roles");
            List<SimpleGrantedAuthority> simpleGrantedAuthorityList = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, simpleGrantedAuthorityList);

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}

