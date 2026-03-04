package com.jobtracker.jobtracker_app.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobtracker.jobtracker_app.entities.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    @Query("""
            SELECT p FROM PasswordResetToken p
            WHERE p.token = :token
              AND p.usedAt IS NULL
              AND p.expiresAt > :now
              AND p.deletedAt IS NULL
            """)
    Optional<PasswordResetToken> findValidByToken(
            @Param("token") String token,
            @Param("now") LocalDateTime now);
}
