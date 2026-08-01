package com.campus.EventManagement.Services;

import com.campus.EventManagement.Entities.RefreshToken;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Repositories.RefreshTokenRepository;
import com.campus.EventManagement.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UserRepository userRepository;

    public RefreshTokenService(
            RefreshTokenRepository repo,
            UserRepository userRepository) {

        this.repo = repo;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new refresh token.
     * Deletes any existing token for that user first.
     */
    public RefreshToken createRefreshToken(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        // One user -> One refresh token
        repo.findByUser(user)
                .ifPresent(repo::delete);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setToken(
                UUID.randomUUID().toString()
        );

        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(7)
        );

        return repo.save(refreshToken);
    }

    /**
     * Find refresh token.
     */
    public RefreshToken findByToken(String token) {

        return repo.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Refresh token not found"
                        ));
    }

    /**
     * Validate expiration.
     */
    public RefreshToken verifyExpiration(
            RefreshToken refreshToken) {

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            repo.delete(refreshToken);

            throw new RuntimeException(
                    "Refresh token expired. Please login again."
            );
        }

        return refreshToken;
    }

    /**
     * Delete refresh token for a user.
     */
    public void deleteByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        repo.findByUser(user)
                .ifPresent(repo::delete);
    }

    /**
     * Optional utility:
     * Delete all expired tokens.
     * Can be scheduled with @Scheduled.
     */
    public void deleteExpiredTokens() {

        repo.findAll()
                .stream()
                .filter(token ->
                        token.getExpiryDate()
                                .isBefore(LocalDateTime.now()))
                .forEach(repo::delete);
    }
}