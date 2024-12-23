package com.example.potholeapp.repository;

import com.example.potholeapp.model.token.PasswordResetToken;
import com.example.potholeapp.model.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    @Transactional
    void deleteByUser(User user);
}
