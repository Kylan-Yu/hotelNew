package com.kylan.hotel.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        try {
            initializeAdminUser();
            initializeRoles();
            initializeUserRoles();
            log.info("User data initialization completed");
        } catch (Exception e) {
            log.error("Failed to initialize user data", e);
        }
    }

    private void initializeAdminUser() {
        // Check if admin user exists
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM sys_user WHERE username = 'admin' AND deleted = 0",
                Integer.class
        );

        if (count == null || count == 0) {
            // Generate BCrypt password hash for "admin"
            String passwordHash = passwordEncoder.encode("admin");
            
            jdbcTemplate.execute("""
                    INSERT INTO sys_user (id, username, password_hash, nickname, mobile, email, status, created_by, updated_by, deleted)
                    VALUES (1, 'admin', ?, '系统管理员', '13800000000', 'admin@example.com', 1, 'system', 'system', 0)
                    """.replace("?", "'" + passwordHash + "'")
            );
            log.info("Created admin user with username: admin, password: admin");
        } else {
            log.info("Admin user already exists");
        }
    }

    private void initializeRoles() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM sys_role WHERE role_code = 'SUPER_ADMIN' AND deleted = 0",
                Integer.class
        );

        if (count == null || count == 0) {
            jdbcTemplate.execute("""
                    INSERT INTO sys_role (id, role_code, role_name, status, created_by, updated_by, deleted)
                    VALUES (1, 'SUPER_ADMIN', '超级管理员', 1, 'system', 'system', 0)
                    """);
            log.info("Created SUPER_ADMIN role");
        } else {
            log.info("SUPER_ADMIN role already exists");
        }
    }

    private void initializeUserRoles() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM sys_user_role WHERE user_id = 1 AND role_id = 1 AND deleted = 0",
                Integer.class
        );

        if (count == null || count == 0) {
            jdbcTemplate.execute("""
                    INSERT INTO sys_user_role (user_id, role_id, created_by, updated_by, deleted)
                    VALUES (1, 1, 'system', 'system', 0)
                    """);
            log.info("Created admin user role assignment");
        } else {
            log.info("Admin user role assignment already exists");
        }
    }
}
