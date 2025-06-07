package com.app.playbooker.service;

import com.app.playbooker.dto.ReviewDTO;
import com.app.playbooker.entity.Review;
import com.app.playbooker.entity.User;
import com.app.playbooker.exceptions.ReviewException;
import com.app.playbooker.repository.ReviewRepository;
import com.app.playbooker.repository.UserRepository;
import com.app.playbooker.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaySpaceService playSpaceService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    public Review createReview(ReviewDTO reviewDTO) {
        try {
            String email = jwtUtil.getEmailFromToken(request);
            User user = userRepository.findByEmail(email);

            Review review = new Review();
            BeanUtils.copyProperties(reviewDTO, review);
            review.setPlaySpaceId(reviewDTO.getPlaySpaceId());
            review.setUserId(user.getId());
            review.setCreatedAt(LocalDateTime.now());
            review = reviewRepository.save(review);
            playSpaceService.updatePlaySpaceRating(reviewDTO.getPlaySpaceId());

            return review;
        } catch (Exception e) {
            throw new ReviewException("Error occurred while creating review due to " + e.getMessage());
        }
    }
}
