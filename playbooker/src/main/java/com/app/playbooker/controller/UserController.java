package com.app.playbooker.controller;

import com.app.playbooker.dto.LoginRequest;
import com.app.playbooker.dto.UserCreationDTO;
import com.app.playbooker.entity.User;
import com.app.playbooker.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/users")
@Log4j2
public class UserController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/handle-oauth-callback")
    public ResponseEntity<Map<String, Object>> handleOauthCallback(@RequestParam String code, HttpServletResponse response) {
        return new ResponseEntity<>(customUserDetailsService.handleOauthCallback(code, response), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        return new ResponseEntity<>(customUserDetailsService.loginHandler(request, httpRequest, response, authentication), HttpStatus.OK);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<Map<String, Object>> getRefreshedAccessToken(@RequestParam String refreshToken) {
        return new ResponseEntity<>(customUserDetailsService.getRefreshedAccessToken(refreshToken), HttpStatus.OK);
    }

    @PostMapping("/create_user")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreationDTO userCreationDTO) {
        return new ResponseEntity<>(customUserDetailsService.createUser(userCreationDTO), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        customUserDetailsService.logoutHandler(refreshToken);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}


