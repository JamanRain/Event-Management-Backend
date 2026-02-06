package com.campus.EventManagement.Dto;

import com.campus.EventManagement.Entities.Role;
import com.campus.EventManagement.Entities.User;

import java.time.LocalDateTime;

public class UserResponse {

    public Long id;
    public String name;
    public String email;
    public Role role;
    public LocalDateTime createdAt;

    public static UserResponse from(User user) {
        UserResponse dto = new UserResponse();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.role = user.getRole();
        dto.createdAt = user.getCreatedAt();
        return dto;
    }
}
