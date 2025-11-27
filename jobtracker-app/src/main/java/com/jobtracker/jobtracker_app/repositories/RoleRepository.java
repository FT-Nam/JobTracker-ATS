package com.jobtracker.jobtracker_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entities.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Boolean existsByName(String name);
    Optional<Role> findByName(String name);
}
