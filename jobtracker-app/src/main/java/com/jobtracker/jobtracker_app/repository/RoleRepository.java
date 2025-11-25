package com.jobtracker.jobtracker_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Boolean existsByName(String name);
}
