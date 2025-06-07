package com.app.playbooker.controller;

import com.app.playbooker.dto.ReviewDTO;
import com.app.playbooker.entity.Review;
import com.app.playbooker.service.PlaySpaceService;
import com.app.playbooker.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static com.app.playbooker.utils.AppConstants.ROLE_ADMIN;
import static com.app.playbooker.utils.AppConstants.ROLE_USER;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Secured({ROLE_ADMIN, ROLE_USER})
    @PostMapping("/create")
    public ResponseEntity<Review> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
    }
}
