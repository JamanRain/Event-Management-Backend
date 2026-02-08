package com.campus.EventManagement.Services;

import com.campus.EventManagement.Entities.PasswordResetToken;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Exceptions.BadRequestException;
import com.campus.EventManagement.Exceptions.ResourceNotFoundException;
import com.campus.EventManagement.Repositories.PasswordResetTokenRepository;
import com.campus.EventManagement.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void createResetToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // invalidate old tokens
        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(resetToken);

        emailService.sendPasswordResetMail(user, token);
    }

    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // delete token after use
        tokenRepository.delete(resetToken);
    }
}

