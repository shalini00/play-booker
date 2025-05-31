package com.app.playbooker.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@Log4j2
public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Keep this secure!
    private static final long EXPIRATION_TIME = 3600000; // 1 hour in ms

    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public Key getSigningKey() {
        return key;
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("JWT Token malformed or expired");
            return false; // Malformed or expired
        }
    }

    public String getJWTToken(HttpServletRequest request) {
        String jwt = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println("cookie- " + cookie);
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        return jwt;
    }

    public Claims getJWTTokenClaims(HttpServletRequest request) {
        String jwt = getJWTToken(request);

        return parseToken(jwt);
    }

    public Claims parseToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody(); // This returns the Claims (payload)
    }

    public String getEmailFromToken(HttpServletRequest request) {
        Claims claims = getJWTTokenClaims(request);
        // typically the subject is the username or email
        return claims.getSubject();
    }
}

