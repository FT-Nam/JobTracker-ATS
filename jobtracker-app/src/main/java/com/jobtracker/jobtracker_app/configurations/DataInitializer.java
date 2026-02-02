package com.jobtracker.jobtracker_app.configurations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.entities.Permission;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.entities.RolePermission;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.repositories.PermissionRepository;
import com.jobtracker.jobtracker_app.repositories.RolePermissionRepository;
import com.jobtracker.jobtracker_app.repositories.RoleRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInitializer implements CommandLineRunner {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Seeding initial data...");
            seedAdminUser();
            log.info("✅ Default admin user created: admin@gmail.com / 123456789");
        } else {
            log.info("Database already initialized. Skipping seeding.");
        }
    }

    @Transactional
    public void seedAdminUser() {
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Administrator role");
        adminRole.setIsActive(true);
        roleRepository.save(adminRole);

        User admin = new User();
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("123456789"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(adminRole);
        userRepository.save(admin);

        adminRole.setCreatedBy(admin.getEmail());
        roleRepository.save(adminRole);

        String adminEmail = admin.getEmail();
        List<Permission> permissions = List.of(
                createPermission("USER_READ", "user", "read", "Read user", adminEmail),
                createPermission("USER_CREATE", "user", "create", "Create user", adminEmail),
                createPermission("USER_UPDATE", "user", "update", "Update user", adminEmail),
                createPermission("USER_DELETE", "user", "delete", "Delete user", adminEmail),
                createPermission("PERMISSION_CREATE", "permission", "create", "Create permission", adminEmail),
                createPermission("PERMISSION_READ", "permission", "read", "Read permission", adminEmail),
                createPermission("PERMISSION_UPDATE", "permission", "update", "Update permission", adminEmail),
                createPermission("PERMISSION_DELETE", "permission", "delete", "Delete permission", adminEmail),
                createPermission("ROLE_CREATE", "role", "create", "Create role", adminEmail),
                createPermission("ROLE_READ", "role", "read", "Read role", adminEmail),
                createPermission("ROLE_UPDATE", "role", "update", "Update role", adminEmail),
                createPermission("ROLE_DELETE", "role", "delete", "Delete role", adminEmail),
                createPermission("JOB_TYPE_CREATE", "job_type", "create", "Create job type", adminEmail),
                createPermission("JOB_TYPE_UPDATE", "job_type", "update", "Update job type", adminEmail),
                createPermission("JOB_TYPE_DELETE", "job_type", "delete", "Delete job type", adminEmail),
                createPermission("JOB_STATUS_CREATE", "job_status", "create", "Create job status", adminEmail),
                createPermission("JOB_STATUS_UPDATE", "job_status", "update", "Update job status", adminEmail),
                createPermission("JOB_STATUS_DELETE", "job_status", "delete", "Delete job status", adminEmail),
                createPermission("PRIORITY_CREATE", "priority", "create", "Create priority", adminEmail),
                createPermission("PRIORITY_UPDATE", "priority", "update", "Update priority", adminEmail),
                createPermission("PRIORITY_DELETE", "priority", "delete", "Delete priority", adminEmail),
                createPermission("NOTIFICATION_TYPE_CREATE", "notification_type", "create", "Create notification type", adminEmail),
                createPermission("NOTIFICATION_TYPE_UPDATE", "notification_type", "update", "Update notification type", adminEmail),
                createPermission("NOTIFICATION_TYPE_DELETE", "notification_type", "delete", "Delete notification type", adminEmail),
                createPermission("NOTIFICATION_PRIORITY_CREATE", "notification_priority", "create", "Create notification priority", adminEmail),
                createPermission("NOTIFICATION_PRIORITY_UPDATE", "notification_priority", "update", "Update notification priority", adminEmail),
                createPermission("NOTIFICATION_PRIORITY_DELETE", "notification_priority", "delete", "Delete notification priority", adminEmail),
                createPermission("INTERVIEW_TYPE_CREATE", "interview_type", "create", "Create interview type", adminEmail),
                createPermission("INTERVIEW_TYPE_UPDATE", "interview_type", "update", "Update interview type", adminEmail),
                createPermission("INTERVIEW_TYPE_DELETE", "interview_type", "delete", "Delete interview type", adminEmail),
                createPermission("INTERVIEW_STATUS_CREATE", "interview_status", "create", "Create interview status", adminEmail),
                createPermission("INTERVIEW_STATUS_UPDATE", "interview_status", "update", "Update interview status", adminEmail),
                createPermission("INTERVIEW_STATUS_DELETE", "interview_status", "delete", "Delete interview status", adminEmail),
                createPermission("INTERVIEW_RESULT_CREATE", "interview_result", "create", "Create interview result", adminEmail),
                createPermission("INTERVIEW_RESULT_UPDATE", "interview_result", "update", "Update interview result", adminEmail),
                createPermission("INTERVIEW_RESULT_DELETE", "interview_result", "delete", "Delete interview result", adminEmail),
                createPermission("EXPERIENCE_LEVEL_CREATE", "experience_level", "create", "Create experience level", adminEmail),
                createPermission("EXPERIENCE_LEVEL_UPDATE", "experience_level", "update", "Update experience level", adminEmail),
                createPermission("EXPERIENCE_LEVEL_DELETE", "experience_level", "delete", "Delete experience level", adminEmail));

        permissionRepository.saveAll(permissions);

        List<RolePermission> rolePermissions = permissions.stream()
                .map(permission -> RolePermission.builder()
                        .role(adminRole)
                        .permission(permission)
                        .build())
                .toList();
        rolePermissionRepository.saveAll(rolePermissions);

        log.info("✅ Admin user created successfully: {}", admin.getEmail());
    }

    private Permission createPermission(String name, String resource, String action, String description, String createdBy) {
        Permission permission = new Permission(null, name, resource, action, description, true, new ArrayList());
        permission.setCreatedBy(createdBy);
        return permission;
    }
}
