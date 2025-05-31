package com.app.playbooker.repository;

import com.app.playbooker.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByPlaySpaceId(String playSpaceId);
}
