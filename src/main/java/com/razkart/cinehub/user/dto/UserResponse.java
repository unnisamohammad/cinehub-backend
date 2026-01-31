package com.razkart.cinehub.user.dto;

import com.razkart.cinehub.user.entity.User;
import com.razkart.cinehub.user.entity.UserRole;
import com.razkart.cinehub.user.entity.UserStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for user information.
 */
public record UserResponse(
        Long id,
        String email,
        String phone,
        String fullName,
        String profileImage,
        UserRole role,
        UserStatus status,
        Boolean emailVerified,
        Boolean phoneVerified,
        LocalDateTime createdAt
) {
    /**
     * Factory method to create UserResponse from User entity.
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getFullName(),
                user.getProfileImage(),
                user.getRole(),
                user.getStatus(),
                user.getEmailVerified(),
                user.getPhoneVerified(),
                user.getCreatedAt()
        );
    }
}
