package com.campus.EventManagement.Controllers;

import com.campus.EventManagement.Dto.UserResponse;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Security.SecurityUtil;
import com.campus.EventManagement.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    // CREATE USER (NO ADMIN CREATION LOGIC HERE – ASSUMED HANDLED)
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {

        return userService.createUser(user)
                .map(saved ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(UserResponse.from(saved)))
                .orElse(ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .build());
    }

    // UPDATE USER (JWT VALIDATED)
    @PutMapping("/update/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody User user) {

        return userService.updateUser(userId, user)
                .map(updated ->
                        ResponseEntity.ok(UserResponse.from(updated)))
                .orElse(ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .build());
    }

    // DELETE USER (JWT VALIDATED)
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {

        return userService.deleteUser(userId)
                .map(r -> ResponseEntity.ok().build())
                .orElse(ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .build());
    }
    @GetMapping("/names/by-role")
    public ResponseEntity<?> getUserNamesByRole(Pageable pageable){
        return ResponseEntity.ok(userService.getUserNamesByRole(pageable));
    }
    @GetMapping("/get-by-role")
    public ResponseEntity<?> getUsersByRole(Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersByRole(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getUser(id)
        );
    }

}


