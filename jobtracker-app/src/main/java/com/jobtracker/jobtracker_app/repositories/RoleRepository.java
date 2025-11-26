package com.jobtracker.jobtracker_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entities.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Boolean existsByName(String name);
    Role findByName(String name);
}
