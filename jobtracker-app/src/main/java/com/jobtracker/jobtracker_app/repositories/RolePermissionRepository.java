package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    List<RolePermission> findByRoleId(String roleId);
    void deleteByRoleId(String roleId);
}




