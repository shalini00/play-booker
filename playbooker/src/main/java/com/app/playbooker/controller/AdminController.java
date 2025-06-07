package com.app.playbooker.controller;

import com.app.playbooker.dto.AdminOverviewMetrics;
import com.app.playbooker.dto.UserCreationDTO;
import com.app.playbooker.service.AdminService;
import com.app.playbooker.service.CustomUserDetailsService;
import com.app.playbooker.service.PlaySpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.app.playbooker.utils.AppConstants.ROLE_ADMIN;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PlaySpaceService playSpaceService;

    @Autowired
    private AdminService adminService;

    @Secured({ROLE_ADMIN})
    @GetMapping("/users/getAll")
    public ResponseEntity<List<UserCreationDTO>> getAllUsers() {
        return ResponseEntity.ok(customUserDetailsService.getAllUsers());
    }

    @Secured({ROLE_ADMIN})
    @PatchMapping("/playspace/toggleVisibility/{id}")
    public ResponseEntity<String> togglePlaySpaceVisibility(@PathVariable String id) {
        playSpaceService.togglePlaySpaceVisibility(id);
        return new ResponseEntity<>("PlaySpace visibility toggled successfully",HttpStatus.OK);
    }

    @Secured({ROLE_ADMIN})
    @GetMapping("/overview")
    public ResponseEntity<AdminOverviewMetrics> overviewMetrics() {
        return new ResponseEntity<>(adminService.getOverviewMetrics(), HttpStatus.OK);
    }
}
