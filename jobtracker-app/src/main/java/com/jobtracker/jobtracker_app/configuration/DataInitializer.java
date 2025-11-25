package com.jobtracker.jobtracker_app.configuration;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.entity.Permission;
import com.jobtracker.jobtracker_app.entity.Role;
import com.jobtracker.jobtracker_app.entity.User;
import com.jobtracker.jobtracker_app.repository.PermissionRepository;
import com.jobtracker.jobtracker_app.repository.RoleRepository;
import com.jobtracker.jobtracker_app.repository.UserRepository;

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

        Permission p1 = new Permission(null, "USER_READ", "user", "read", "Read user", true);
        Permission p2 = new Permission(null, "USER_CREATE", "user", "create", "Create user", true);
        Permission p3 = new Permission(null, "USER_UPDATE", "user", "update", "Update user", true);
        Permission p4 = new Permission(null, "USER_DELETE", "user", "delete", "Delete user", true);

        p1.setCreatedBy(admin.getEmail());
        p2.setCreatedBy(admin.getEmail());
        p3.setCreatedBy(admin.getEmail());
        p4.setCreatedBy(admin.getEmail());

        permissionRepository.saveAll(List.of(p1, p2, p3, p4));

        adminRole.setPermissions(List.of(p1, p2, p3, p4));

        roleRepository.save(adminRole);

        log.info("✅ Admin user created successfully: {}", admin.getEmail());
    }
}
