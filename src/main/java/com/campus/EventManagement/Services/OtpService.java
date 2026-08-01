package com.campus.EventManagement.Services;

import com.campus.EventManagement.Entities.OtpToken;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Repositories.OtpTokenRepository;
import com.campus.EventManagement.Repositories.UserRepository;
import com.campus.EventManagement.Security.CustomUserDetails;
import com.campus.EventManagement.Security.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public OtpService(OtpTokenRepository otpTokenRepository,
                      UserRepository userRepository,
                      EmailService emailService,
                      JwtUtil jwtUtil) {
        this.otpTokenRepository = otpTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000)); // 6 digits
    }

    public void sendOtp(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Optional: invalidate old OTPs
        otpTokenRepository.deleteByEmail(email);

        String otp = generateOtp();

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setUsed(false);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpTokenRepository.save(token);

        emailService.sendSimpleMail(
                email,
                "Your Login OTP",
                "Your OTP is: " + otp + " (valid for 5 minutes)"
        );
    }

    public String verifyOtpAndLogin(String email, String otp) {

        OtpToken token = otpTokenRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (token.isUsed())
            throw new RuntimeException("OTP already used");

        if (token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("OTP expired");

        if (!token.getOtp().equals(otp))
            throw new RuntimeException("Invalid OTP");

        token.setUsed(true);
        otpTokenRepository.save(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomUserDetails cud = new CustomUserDetails(user);

        return jwtUtil.generateToken(cud);
    }
}