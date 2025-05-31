package com.app.playbooker.controller;

import com.app.playbooker.dto.SlotInfoDTO;
import com.app.playbooker.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.app.playbooker.utils.AppConstants.ROLE_ADMIN;
import static com.app.playbooker.utils.AppConstants.ROLE_USER;

@RestController
@RequestMapping("/api/v1/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @Secured({ROLE_ADMIN, ROLE_USER})
    @GetMapping("/getAll")
    public ResponseEntity<?> getSlotInfo(
            @RequestParam String playspaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date.isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body("Cannot check slots for past dates.");
        }

        List<SlotInfoDTO> slots = availabilityService.getSlotInfo(playspaceId, date);
        return ResponseEntity.ok(Collections.singletonMap("slotInfo", slots));
    }

}
