package com.campus.EventManagement.Security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static CustomUserDetails getCurrentUser() {
        return (CustomUserDetails)
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static String getCurrentRole() {
        return getCurrentUser()
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority();
    }
}

