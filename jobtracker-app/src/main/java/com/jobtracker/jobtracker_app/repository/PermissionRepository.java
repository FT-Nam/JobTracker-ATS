package com.jobtracker.jobtracker_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    Boolean existsByName(String name);
}
