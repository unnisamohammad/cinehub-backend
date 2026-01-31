package com.razkart.cinehub.user.service;

import com.razkart.cinehub.common.exception.BusinessException;
import com.razkart.cinehub.common.exception.ResourceNotFoundException;
import com.razkart.cinehub.common.util.JwtUtil;
import com.razkart.cinehub.user.dto.LoginRequest;
import com.razkart.cinehub.user.dto.LoginResponse;
import com.razkart.cinehub.user.dto.RegisterRequest;
import com.razkart.cinehub.user.dto.UserResponse;
import com.razkart.cinehub.user.entity.User;
import com.razkart.cinehub.user.entity.UserRole;
import com.razkart.cinehub.user.entity.UserStatus;
import com.razkart.cinehub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.email());

        if (userRepository.existsByEmail(request.email())) throw new BusinessException("Email is already registered");
        if (request.phone() != null && userRepository.existsByPhone(request.phone())) throw new BusinessException("Phone number is already registered");

        User user = User.builder()
                .email(request.email()).phone(request.phone())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName()).role(UserRole.CUSTOMER).status(UserStatus.ACTIVE)
                .emailVerified(false).phoneVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered: {}", savedUser.getId());
        return UserResponse.from(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Invalid password for: {}", request.email());
            throw new BusinessException("Invalid email or password");
        }

        if (!user.isActive()) {
            log.warn("Inactive user login attempt: {}", request.email());
            throw new BusinessException("Account is not active. Please contact support.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        log.info("User logged in: {}", user.getEmail());
        return new LoginResponse(token, jwtUtil.getExpirationInSeconds(), UserResponse.from(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
        return UserResponse.from(findUser(id));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, String fullName, String phone, String profileImage) {
        log.info("Updating profile: {}", userId);

        User user = findUser(userId);

        if (phone != null && !phone.equals(user.getPhone())) {
            if (userRepository.existsByPhone(phone)) throw new BusinessException("Phone number is already in use");
            user.setPhone(phone);
            user.setPhoneVerified(false);
        }
        if (fullName != null) user.setFullName(fullName);
        if (profileImage != null) user.setProfileImage(profileImage);

        User updatedUser = userRepository.save(user);
        log.info("Profile updated: {}", userId);
        return UserResponse.from(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Changing password: {}", userId);

        User user = findUser(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) throw new BusinessException("Current password is incorrect");

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed: {}", userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }
}
