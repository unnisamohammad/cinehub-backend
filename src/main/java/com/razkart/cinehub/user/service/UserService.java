package com.razkart.cinehub.user.service;

import com.razkart.cinehub.user.dto.LoginRequest;
import com.razkart.cinehub.user.dto.LoginResponse;
import com.razkart.cinehub.user.dto.RegisterRequest;
import com.razkart.cinehub.user.dto.UserResponse;
import com.razkart.cinehub.user.entity.User;

import java.util.Optional;

/**
 * Service interface for user operations.
 */
public interface UserService {

    /**
     * Register a new user.
     */
    UserResponse register(RegisterRequest request);

    /**
     * Authenticate user and return JWT token.
     */
    LoginResponse login(LoginRequest request);

    /**
     * Get user by ID.
     */
    UserResponse getUserById(Long id);

    /**
     * Get user by email.
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Update user profile.
     */
    UserResponse updateProfile(Long userId, String fullName, String phone, String profileImage);

    /**
     * Change user password.
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * Check if email is already registered.
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone is already registered.
     */
    boolean existsByPhone(String phone);
}
