package com.jobtracker.jobtracker_app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
    // N+1 QUERY
//    @Query(value = "SELECT * FROM users u " +
//            "WHERE u.deleted_at IS NULL " +
//            "AND (:keyword IS NULL OR " +
//            "u.email LIKE CONCAT('%', :keyword, '%') OR " +
//            "u.first_name LIKE CONCAT('%', :keyword, '%') OR " +
//            "u.last_name LIKE CONCAT('%', :keyword, '%') OR " +
//            "u.phone LIKE CONCAT('%', :keyword, '%')" +
//            ") " +
//            "AND (:roleId IS NULL OR u.role_id = :roleId) " +
//            "AND (:isActive IS NULL OR u.is_active = :isActive) " +
//            "AND (:emailVerified IS NULL OR u.email_verified = :emailVerified) " +
//            "ORDER BY u.created_at DESC", nativeQuery = true)

    @Query(value = "SELECT u FROM User u " +
            "JOIN FETCH u.role r " +
            "WHERE u.deletedAt IS NULL " +
            "AND (:keyword IS NULL OR " +
            "u.email LIKE CONCAT('%', :keyword, '%') OR " +
            "u.firstName LIKE CONCAT('%', :keyword, '%') OR " +
            "u.lastName LIKE CONCAT('%', :keyword, '%') OR " +
            "u.phone LIKE CONCAT('%', :keyword, '%')" +
            ") " +
            "AND (:roleId IS NULL OR r.id = :roleId) " +
            "AND (:isActive IS NULL OR u.isActive = :isActive) " +
            "AND (:emailVerified IS NULL OR u.emailVerified = :emailVerified) " +
            "ORDER BY u.createdAt DESC")
    Page<User> findAllAndSearch(
            @Param("keyword") String keyword,
            @Param("roleId") String roleId,
            @Param("isActive") Boolean isActive,
            @Param("emailVerified") Boolean emailVerified,
            Pageable pageable
    );

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findAllByRoleId(String roleId);
}
