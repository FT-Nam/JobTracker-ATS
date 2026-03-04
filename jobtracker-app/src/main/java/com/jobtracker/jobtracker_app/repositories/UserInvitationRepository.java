package com.jobtracker.jobtracker_app.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobtracker.jobtracker_app.entities.UserInvitation;

public interface UserInvitationRepository extends JpaRepository<UserInvitation, String> {

    @Query("""
            SELECT ui FROM UserInvitation ui
            WHERE ui.token = :token
              AND ui.usedAt IS NULL
              AND ui.expiresAt > :now
              AND ui.deletedAt IS NULL
            """)
    Optional<UserInvitation> findValidByToken(
            @Param("token") String token,
            @Param("now") LocalDateTime now);
}
