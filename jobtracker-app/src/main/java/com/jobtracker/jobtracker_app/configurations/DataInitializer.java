package com.jobtracker.jobtracker_app.configurations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.entities.Permission;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.entities.RolePermission;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.enums.StatusType;
import com.jobtracker.jobtracker_app.repositories.ApplicationStatusRepository;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
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
    CompanyRepository companyRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    ApplicationStatusRepository applicationStatusRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Seeding initial data...");
            seedApplicationStatuses();
            seedAdminUser();
            log.info("✅ Default admin user created: admin@gmail.com / 123456789");
        } else {
            log.info("Database already initialized. Skipping seeding.");
        }
    }

    @Transactional
    public void seedAdminUser() {
        Company company = companyRepository.findAll().stream().findFirst().orElseGet(() -> {
            Company c = Company.builder()
                    .name("Default Company")
                    .isVerified(true)
                    .build();
            return companyRepository.save(c);
        });

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Administrator role");
        adminRole.setIsActive(true);
        roleRepository.save(adminRole);

        Role companyAdminRole = new Role();
        companyAdminRole.setName("COMPANY_ADMIN");
        companyAdminRole.setDescription("Company administrator (self-signup)");
        companyAdminRole.setIsActive(true);
        roleRepository.save(companyAdminRole);

        User admin = new User();
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("123456789"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setCompany(company);
        admin.setRole(adminRole);
        userRepository.save(admin);

        adminRole.setCreatedBy(admin.getEmail());
        roleRepository.save(adminRole);
        companyAdminRole.setCreatedBy(admin.getEmail());
        roleRepository.save(companyAdminRole);

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
                createPermission("ROLE_DELETE", "role", "delete", "Delete role", adminEmail));

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

    @Transactional
    public void seedApplicationStatuses() {
        if (applicationStatusRepository.count() == 0) {
            log.info("Seeding application statuses...");
            String systemUser = "system";

            List<ApplicationStatus> statuses = List.of(
                    createApplicationStatus("NEW", "Mới", "Ứng viên vừa nộp đơn", "#6B7280", 1,
                            StatusType.APPLIED, false, true, systemUser),
                    createApplicationStatus("SCREENING", "Sàng lọc", "Đang sàng lọc hồ sơ", "#3B82F6", 2,
                            StatusType.SCREENING, false, false, systemUser),
                    createApplicationStatus("INTERVIEWING", "Phỏng vấn", "Đang trong quá trình phỏng vấn", "#F59E0B", 3,
                            StatusType.INTERVIEW, false, false, systemUser),
                    createApplicationStatus("OFFERED", "Đã đề xuất", "Đã gửi offer cho ứng viên", "#8B5CF6", 4,
                            StatusType.OFFER, false, false, systemUser),
                    createApplicationStatus("HIRED", "Đã tuyển", "Ứng viên đã được tuyển", "#10B981", 5,
                            StatusType.HIRED, true, false, systemUser),
                    createApplicationStatus("REJECTED", "Từ chối", "Ứng viên bị từ chối", "#EF4444", 6,
                            StatusType.REJECTED, true, false, systemUser)
            );
            
            applicationStatusRepository.saveAll(statuses);
            log.info("✅ Application statuses seeded: {} statuses", statuses.size());
        }
    }

    private ApplicationStatus createApplicationStatus(
            String name,
            String displayName,
            String description,
            String color,
            Integer sortOrder,
            StatusType statusType,
            boolean isTerminal,
            boolean isDefault,
            String createdBy) {
        ApplicationStatus status = ApplicationStatus.builder()
                .name(name)
                .displayName(displayName)
                .description(description)
                .color(color)
                .sortOrder(sortOrder)
                .statusType(statusType)
                .isTerminal(isTerminal)
                .isDefault(isDefault)
                .isActive(true)
                .build();
        status.setCreatedBy(createdBy);
        return status;
    }

    private Permission createPermission(String name, String resource, String action, String description, String createdBy) {
        Permission permission = new Permission(null, name, resource, action, description, true, new ArrayList());
        permission.setCreatedBy(createdBy);
        return permission;
    }
}
