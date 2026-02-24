package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.RolePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    boolean existsByRole_IdAndPermission_Id(String roleId, String permissionId);
    Page<RolePermission> findByRole_Id(String roleId, Pageable pageable);
    List<RolePermission> findByRole_Id(String roleId);
    void deleteByRole_IdAndPermission_Id(String roleId, String permissionId);
    void deleteByRole_Id(String roleId);
}





