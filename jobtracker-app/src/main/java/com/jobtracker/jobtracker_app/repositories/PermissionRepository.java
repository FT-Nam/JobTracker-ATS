package com.jobtracker.jobtracker_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entities.Permission;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    Boolean existsByName(String name);
}
