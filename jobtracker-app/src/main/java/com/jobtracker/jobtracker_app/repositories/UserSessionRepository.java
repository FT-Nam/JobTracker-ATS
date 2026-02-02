package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
}




