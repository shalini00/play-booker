package com.app.playbooker.controller;

import com.app.playbooker.dto.LoginRequest;
import com.app.playbooker.dto.UserCreationDTO;
import com.app.playbooker.entity.RefreshToken;
import com.app.playbooker.entity.Roles;
import com.app.playbooker.entity.User;
import com.app.playbooker.enums.Providers;
import com.app.playbooker.exceptions.AuthException;
import com.app.playbooker.repository.RefreshTokenRepository;
import com.app.playbooker.repository.RolesRepository;
import com.app.playbooker.repository.UserRepository;
import com.app.playbooker.service.CustomUserDetailsService;
import com.app.playbooker.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/users")
@Log4j2
public class UserController {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectURI;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/handle-oauth-callback")
    public ResponseEntity<Map<String, Object>> handleOauthCallback(@RequestParam String code, HttpServletResponse response) {
        try {
            // - Get access token/id_token by calling google API
            String tokenFetchUrl = "https://oauth2.googleapis.com/token";
            Map<String, String> request = new HashMap<>();
            request.put("code", code);
            request.put("client_id", clientId);
            request.put("client_secret", clientSecret);
            request.put("redirect_uri", redirectURI);
            request.put("grant_type", "authorization_code");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(request, httpHeaders);
            ResponseEntity<Map<?, ?>> tokenFetchResponse = restTemplate.exchange(tokenFetchUrl, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Map<?, ?>>() {
            });
            log.info("tokenFetchResponse: {}", tokenFetchResponse.getBody());

            // - Validate id token by calling google API
            String validateTokenUrl = String.format("https://oauth2.googleapis.com/tokeninfo?id_token=%s", tokenFetchResponse.getBody().get("id_token"));
            ResponseEntity<Map<?, ?>> validateTokenResponse = restTemplate.exchange(validateTokenUrl, HttpMethod.GET, new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<Map<?, ?>>() {
            });
            log.info("validateTokenResponse: {}", validateTokenResponse.getBody());

            // - Fetch the email from validation response and check if user exists. If not then create a new user.
            String email = String.valueOf(validateTokenResponse.getBody().get("email"));
            User existingUser = userRepository.findByEmail(email);
            if (Objects.isNull(existingUser)) {
                User user = new User();
                String name = validateTokenResponse.getBody().get("name").toString();
                user.setName(name);
                user.setEmail(email);
                user.setUsername(email);
                user.setPassword("default_password");
                user.setProvider(Providers.GOOGLE);
                Roles roles = rolesRepository.findByName("ROLE_USER").get();
                user.setRoles(Set.of(roles));
                userRepository.save(user);
                log.info("User saved successfully");
            }

            // - Create JWT Token using JWT util
            return handleRefreshToken(email, response);

        } catch (RestClientException e) {
            log.error("Failed to call Google API");
            throw new AuthException("Failed to call Google API", e);
        } catch (Exception e) {
            log.error("Error in handling oauth callback");
            throw new AuthException(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Create HTTP session manually if needed
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return handleRefreshToken(request.getEmail(), response);
        } catch (AuthenticationException ex) {
            System.out.println(61);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<Map<String, Object>> getRefreshedAccessToken(@RequestParam String refreshToken) {
        RefreshToken refreshTokenObject = refreshTokenRepository.findByRefreshToken(refreshToken).get();
        String email = refreshTokenObject.getEmail();
        return new ResponseEntity<>(Map.of("access_token", jwtUtil.generateToken(new HashMap<>(), email)), HttpStatus.OK);
    }

    @PostMapping("/create_user")
    public ResponseEntity<String> createUser(@RequestBody UserCreationDTO userCreationDTO) {
        customUserDetailsService.createUser(userCreationDTO);
        return new ResponseEntity<>("User created successfully.", HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        RefreshToken refreshTokenObject = refreshTokenRepository.findByRefreshToken(refreshToken).get();
        String email = refreshTokenObject.getEmail();
        refreshTokenRepository.deleteAllByEmail(email);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    public ResponseEntity<Map<String, Object>> handleRefreshToken(String email, HttpServletResponse response) {
        String accessToken = jwtUtil.generateToken(new HashMap<>(), email);
        List<RefreshToken> existingRefreshTokenList = refreshTokenRepository.findByEmail(email);
        if (!existingRefreshTokenList.isEmpty()) {
            refreshTokenRepository.deleteAllByEmail(email);
        }

        String refreshToken = UUID.randomUUID().toString();
        RefreshToken refreshTokenObject = new RefreshToken(refreshToken, email);
        refreshTokenRepository.save(refreshTokenObject);
        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("access_token", accessToken);
        finalResponse.put("refresh_token", refreshToken);
        ResponseCookie cookie = ResponseCookie.from("jwt", accessToken)
                .httpOnly(true)
                .secure(false) // true in production (HTTPS)
                .path("/")
                .sameSite("Lax") // "None" if cross-origin
                .maxAge(Duration.ofHours(1))
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
        return new ResponseEntity<>(finalResponse, HttpStatus.OK);
    }


}


