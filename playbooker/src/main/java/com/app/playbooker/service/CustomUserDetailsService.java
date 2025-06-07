package com.app.playbooker.service;

import com.app.playbooker.dto.LoginRequest;
import com.app.playbooker.dto.UserCreationDTO;
import com.app.playbooker.entity.RefreshToken;
import com.app.playbooker.entity.Roles;
import com.app.playbooker.entity.User;
import com.app.playbooker.enums.OtpType;
import com.app.playbooker.enums.Providers;
import com.app.playbooker.exceptions.AuthException;
import com.app.playbooker.exceptions.PlaySpaceNotFoundException;
import com.app.playbooker.exceptions.UserException;
import com.app.playbooker.repository.RefreshTokenRepository;
import com.app.playbooker.repository.RolesRepository;
import com.app.playbooker.repository.UserRepository;
import com.app.playbooker.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.app.playbooker.utils.AppConstants.ROLE_ADMIN;

@Service
@Data
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    @Qualifier("bCryptPasswordEncoder")
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectURI;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                email,
                user.getPassword(),
                authorities
        );

    }

    public User createUser(UserCreationDTO userCreationDTO) {
        try {
            if (Objects.nonNull(userRepository.findByEmail(userCreationDTO.getEmail()))) {
                throw new UserException("Email already exists.");
            }
            if (userRepository.existsByUsername(userCreationDTO.getUsername())) {
                throw new UserException("Username already exists.");
            }
            User user = new User();
            BeanUtils.copyProperties(userCreationDTO, user);
            user.setPassword(bCryptPasswordEncoder.encode(userCreationDTO.getPassword()));
            Roles roles = rolesRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new UserException("Failed to create user, due to role not found"));
            user.setRoles(Set.of(roles));
            user.setProvider(Providers.DEFAULT);
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            otpService.generateAndSendOtp(user, OtpType.EMAIL);
            return user;
        }
        catch (Exception e) {
            throw new UserException("Failed to create user, due to - " + e.getLocalizedMessage());
        }
    }

    public Map<String, Object> handleOauthCallback(String code, HttpServletResponse response) {
        try {
            // - Get access token/id_token by calling google API
            String tokenFetchUrl = "https://oauth2.googleapis.com/toke";
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
            String email = String.valueOf(Objects.requireNonNull(validateTokenResponse.getBody()).get("email"));
            User existingUser = userRepository.findByEmail(email);
            if (Objects.isNull(existingUser)) {
                User user = new User();
                String name = validateTokenResponse.getBody().get("name").toString();
                user.setName(name);
                user.setEmail(email);
                user.setUsername(email);
                user.setPassword("default_password");
                user.setProvider(Providers.GOOGLE);
                Roles roles = rolesRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new AuthException("Error in handling oauth callback due to role not found"));
                user.setRoles(Set.of(roles));
                user.setEmailVerified(true);
                userRepository.save(user);
                log.info("User saved successfully");
            }

            // - Create JWT Token using JWT util
            return handleRefreshToken(email, response);

        }
        catch (RestClientException e) {
            log.error("Failed to call Google API");
            throw new AuthException("Failed to call Google API", e);
        }
        catch (Exception e) {
            log.error("Error in handling oauth callback");
            throw new AuthException(e.getMessage());
        }
    }

    public Map<String, Object> getRefreshedAccessToken(String refreshToken) {
        RefreshToken refreshTokenObject = refreshTokenRepository.findByRefreshToken(refreshToken).get();
        String email = refreshTokenObject.getEmail();
        return Map.of("access_token", jwtUtil.generateToken(new HashMap<>(), email));
    }

    public Map<String, Object> handleRefreshToken(String email, HttpServletResponse response) {
        User user = userRepository.findByEmail(email);
        String accessToken = jwtUtil.generateToken(Map.of("roles", user.getRoles().stream().map(Roles::getName).toList()), email);
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
        return finalResponse;
    }

    public Map<String, Object> loginHandler(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response, Authentication authentication) {
        try {
            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Create HTTP session manually if needed
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return handleRefreshToken(request.getEmail(), response);
        } catch (AuthenticationException ex) {
            log.error("Invalid credentials");
            throw new AuthException("Login Failed due to - " + ex.getMessage());
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isAdminUser(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(ROLE_ADMIN));
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new UserException("User not found for id: " + id));
    }

    public void logoutHandler(String refreshToken) {
        RefreshToken refreshTokenObject = refreshTokenRepository.findByRefreshToken(refreshToken).get();
        String email = refreshTokenObject.getEmail();
        refreshTokenRepository.deleteAllByEmail(email);
    }

    public List<UserCreationDTO> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(user -> {
                    UserCreationDTO userCreationDTO = new UserCreationDTO();
                    BeanUtils.copyProperties(user, userCreationDTO);
                    return userCreationDTO;
                })
                .collect(Collectors.toList());
    }

    public long getNewUserSignups(LocalDateTime fromDate, int daysBefore) {
        return userRepository.countByCreatedAtAfter(fromDate.minusDays(daysBefore));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserException("User not found for this username: " + username));
    }
}
