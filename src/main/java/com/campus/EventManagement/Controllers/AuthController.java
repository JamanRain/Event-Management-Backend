package com.campus.EventManagement.Controllers;

import com.campus.EventManagement.Entities.RefreshToken;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Security.CustomUserDetails;
import com.campus.EventManagement.Security.JwtUtil;
import com.campus.EventManagement.Security.SecurityUtil;
import com.campus.EventManagement.Services.OtpService;
import com.campus.EventManagement.Services.PasswordResetService;
import com.campus.EventManagement.Services.RefreshTokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PasswordResetService passwordResetService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;

    public AuthController(
            AuthenticationManager authManager,
            JwtUtil jwtUtil,
            PasswordResetService passwordResetService,
            RefreshTokenService refreshTokenService,
            OtpService otpService) {

        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.passwordResetService = passwordResetService;
        this.refreshTokenService = refreshTokenService;
        this.otpService = otpService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> req) {

        Authentication auth =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                req.get("email"),
                                req.get("password")
                        )
                );

        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        refreshTokenService.deleteByUser(
                user.getId()
        );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getId()
                );

        String role =
                user.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority();

        return ResponseEntity.ok(
                Map.of(
                        "token",
                        jwtUtil.generateToken(user),

                        "refreshToken",
                        refreshToken.getToken(),

                        "role",
                        role,

                        "userId",
                        user.getId(),

                        "email",
                        user.getUsername()
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody Map<String, String> req) {

        String requestToken =
                req.get("refreshToken");

        RefreshToken refreshToken =
                refreshTokenService.findByToken(
                        requestToken
                );

        refreshTokenService
                .verifyExpiration(
                        refreshToken
                );

        User user =
                refreshToken.getUser();

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        String newAccessToken =
                jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(
                Map.of(
                        "accessToken",
                        newAccessToken
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        Long userId =
                SecurityUtil.getCurrentUserId();

        refreshTokenService.deleteByUser(
                userId
        );

        return ResponseEntity.ok(
                "Logged out successfully"
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> req) {

        passwordResetService.createResetToken(
                req.get("email")
        );

        return ResponseEntity.ok(
                "Password reset link sent"
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> req) {

        passwordResetService.resetPassword(
                req.get("token"),
                req.get("newPassword")
        );

        return ResponseEntity.ok(
                "Password updated successfully"
        );
    }

    @PostMapping("/login/otp/request")
    public ResponseEntity<?> requestOtp(
            @RequestBody Map<String, String> req) {

        otpService.sendOtp(
                req.get("email")
        );

        return ResponseEntity.ok(
                "OTP sent successfully"
        );
    }

    @PostMapping("/login/otp/verify")
    public ResponseEntity<?> verifyOtp(
            @RequestBody Map<String, String> req) {

        String token =
                otpService.verifyOtpAndLogin(
                        req.get("email"),
                        req.get("otp")
                );

        return ResponseEntity.ok(
                Map.of(
                        "token",
                        token
                )
        );
    }
}