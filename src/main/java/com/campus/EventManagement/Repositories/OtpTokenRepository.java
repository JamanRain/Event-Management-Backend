package com.campus.EventManagement.Repositories;

import com.campus.EventManagement.Entities.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByEmailOrderByCreatedAtDesc(String email);

    void deleteByEmail(String email);
}
