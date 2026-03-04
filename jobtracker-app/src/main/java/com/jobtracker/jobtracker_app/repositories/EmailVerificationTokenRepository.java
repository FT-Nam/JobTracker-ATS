package com.jobtracker.jobtracker_app.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobtracker.jobtracker_app.entities.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {

    @Query("""
            SELECT e FROM EmailVerificationToken e
            WHERE e.token = :token
              AND e.usedAt IS NULL
              AND e.expiresAt > :now
              AND e.deletedAt IS NULL
            """)
    Optional<EmailVerificationToken> findValidByToken(
            @Param("token") String token,
            @Param("now") LocalDateTime now);
}
