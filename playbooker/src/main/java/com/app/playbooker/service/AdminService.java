package com.app.playbooker.service;

import com.app.playbooker.dto.AdminOverviewMetrics;
import com.app.playbooker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AdminService {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PlaySpaceService playSpaceService;

    @Value("${latest.user.signup.days}")
    private int daysBefore;

    public AdminOverviewMetrics getOverviewMetrics() {
        Long userSignUpCount = customUserDetailsService.getNewUserSignups(LocalDateTime.now(), daysBefore);
        Long bookingCount = bookingService.getTotalBookings();
        BigDecimal totalRevenue = bookingService.getTotalRevenue();
        Long activePlaySpaceCount = playSpaceService.getActivePlaySpaceCount();

        AdminOverviewMetrics adminOverviewMetrics = new AdminOverviewMetrics();
        adminOverviewMetrics.setNewUserSignups(userSignUpCount);
        adminOverviewMetrics.setTotalBookings(bookingCount);
        adminOverviewMetrics.setTotalRevenue(totalRevenue);
        adminOverviewMetrics.setActivePlaySpaces(activePlaySpaceCount);

        return adminOverviewMetrics;
    }

}
