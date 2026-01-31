package com.razkart.cinehub.user.controller;

import com.razkart.cinehub.common.dto.ApiResponse;
import com.razkart.cinehub.user.dto.LoginRequest;
import com.razkart.cinehub.user.dto.LoginResponse;
import com.razkart.cinehub.user.dto.RegisterRequest;
import com.razkart.cinehub.user.dto.UserResponse;
import com.razkart.cinehub.user.entity.User;
import com.razkart.cinehub.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user authentication and profile management.
 */
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "User", description = "User authentication and profile management APIs")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse user = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "Registration successful"));
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @GetMapping("/users/me")
    @Operation(summary = "Get current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal User currentUser) {

        UserResponse user = UserResponse.from(currentUser);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping("/users/me")
    @Operation(summary = "Update current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateProfileRequest request) {

        UserResponse user = userService.updateProfile(
                currentUser.getId(),
                request.fullName(),
                request.phone(),
                request.profileImage()
        );
        return ResponseEntity.ok(ApiResponse.success(user, "Profile updated successfully"));
    }

    @PostMapping("/users/me/change-password")
    @Operation(summary = "Change password", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(currentUser.getId(), request.oldPassword(), request.newPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * Request DTO for profile update.
     */
    public record UpdateProfileRequest(
            String fullName,
            String phone,
            String profileImage
    ) {}

    /**
     * Request DTO for password change.
     */
    public record ChangePasswordRequest(
            @jakarta.validation.constraints.NotBlank(message = "Old password is required")
            String oldPassword,

            @jakarta.validation.constraints.NotBlank(message = "New password is required")
            @jakarta.validation.constraints.Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
            String newPassword
    ) {}
}
