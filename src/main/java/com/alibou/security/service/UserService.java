package com.alibou.security.service;

import com.alibou.security.request.ChangePasswordRequest;
import com.alibou.security.model.user.User;
import com.alibou.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public void changePassword(ChangePasswordRequest request, String username) {
        logger.debug("Starting password change process for user: {}", username);

        User user = repository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", username);
                    return new IllegalStateException("User not found");
                });
        logger.debug("User found: {}", user.getEmail());

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            logger.warn("Current password does not match for user: {}", user.getEmail());
            throw new IllegalStateException("Wrong password");
        }
        logger.debug("Current password matches");

        // Kiểm tra mật khẩu mới và xác nhận
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            logger.warn("New password and confirmation do not match for user: {}", user.getEmail());
            throw new IllegalStateException("Passwords are not the same");
        }
        logger.debug("New password and confirmation match");

        // Cập nhật mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
        logger.debug("Password updated successfully for user: {}", user.getEmail());
    }
}
