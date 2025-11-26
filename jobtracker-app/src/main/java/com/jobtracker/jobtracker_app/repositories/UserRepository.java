package com.jobtracker.jobtracker_app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entities.User;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findAllByRoleId(String roleId);
}
