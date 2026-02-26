package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Notification;
import com.jobtracker.jobtracker_app.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    Optional<Notification> findByIdAndCompany_IdAndUser_Id(String id, String companyId, String userId);

    // Modifying: query này không phải SELECT, mà là UPDATE / DELETE / INSERT
    @Modifying
    @Query("""
    UPDATE Notification n
    SET n.isRead = true
    WHERE n.company.id = :companyId
      AND n.user.id = :userId
      AND n.isRead = false
    """)
    int markAllAsRead(@Param("companyId") String companyId,
                      @Param("userId") String userId);

    @Query("""
    SELECT n
    FROM Notification n
    WHERE n.user.id = :userId
      AND n.company.id = :companyId
      AND (:isRead IS NULL OR n.isRead = :isRead)
      AND (:type IS NULL OR n.type = :type)
      AND (:applicationId IS NULL OR n.application.id = :applicationId)
      ORDER BY n.createdAt DESC
    """)
    Page<Notification> searchNotification(
            @Param("userId") String userId,
            @Param("companyId") String companyId,
            @Param("isRead") Boolean isRead,
            @Param("type") NotificationType type,
            @Param("applicationId") String applicationId,
            Pageable pageable);
}




