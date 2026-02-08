package com.campus.EventManagement.Services;

import com.campus.EventManagement.Entities.RefreshToken;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Repositories.RefreshTokenRepository;
import com.campus.EventManagement.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;

    public RefreshTokenService(RefreshTokenRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public RefreshToken createRefreshToken(User user) {

        repo.deleteByUser(user);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusDays(7));

        return repo.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            repo.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }
        return token;
    }
    public RefreshToken findByToken(String token) {
        return repo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }
    public void deleteByUser(User user) {
        repo.deleteByUser(user);
    }
}

