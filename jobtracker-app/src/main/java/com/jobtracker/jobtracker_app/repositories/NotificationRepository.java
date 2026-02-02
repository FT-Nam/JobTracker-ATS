package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
}




