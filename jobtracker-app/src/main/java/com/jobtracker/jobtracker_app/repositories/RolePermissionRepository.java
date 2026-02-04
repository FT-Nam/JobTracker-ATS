package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Permission;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.entities.RolePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    boolean existsByRoleAndPermission(Role role, Permission permission);
    Page<RolePermission> findByRole(Role role, Pageable pageable);
    List<RolePermission> findByRole(Role role);
    void deleteByRoleAndPermission(Role role, Permission permission);
    void deleteByRole(Role role);
}





