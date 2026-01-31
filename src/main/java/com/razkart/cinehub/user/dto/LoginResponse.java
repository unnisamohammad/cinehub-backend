package com.razkart.cinehub.user.dto;

/**
 * Response DTO for successful login.
 */
public record LoginResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        UserResponse user
) {
    public LoginResponse(String accessToken, Long expiresIn, UserResponse user) {
        this(accessToken, "Bearer", expiresIn, user);
    }
}
