package com.razkart.cinehub.user.repository;

import com.razkart.cinehub.user.entity.User;
import com.razkart.cinehub.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by phone number.
     */
    Optional<User> findByPhone(String phone);

    /**
     * Check if a user exists with the given email.
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given phone number.
     */
    boolean existsByPhone(String phone);

    /**
     * Find active user by email.
     */
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
}
