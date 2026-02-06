package com.campus.EventManagement.Controllers;

import com.campus.EventManagement.Security.CustomUserDetails;
import com.campus.EventManagement.Security.SecurityConfig;

import com.campus.EventManagement.Security.JwtUtil;
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

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.get("email"), req.get("password"))
        );

        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "token", jwtUtil.generateToken(user),
                "role", user.getAuthorities().iterator().next().getAuthority()
        ));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT is stateless – client deletes token
        return ResponseEntity.ok("Logout successful");
    }
}
