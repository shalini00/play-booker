package com.app.playbooker.repository;

import com.app.playbooker.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    List<RefreshToken> findByEmail(String email);

    @Transactional
    void deleteAllByEmail(String email);
}
