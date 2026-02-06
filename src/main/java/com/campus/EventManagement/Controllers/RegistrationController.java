package com.campus.EventManagement.Controllers;

import com.campus.EventManagement.Services.EventService;
import com.campus.EventManagement.Services.RegistrationService;
import com.campus.EventManagement.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/register/{eventId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> register(@PathVariable Long eventId) {

        return registrationService.registerForEvent(eventId)
                .map(r -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{eventId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> cancel(@PathVariable Long eventId) {

        return registrationService.cancelRegistration(eventId)
                .map(r -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.badRequest().build());
    }
    @GetMapping("/my-events")
    public ResponseEntity<?> getMyRegisteredEvents(Pageable pageable) {
        return ResponseEntity.ok(registrationService.getMyRegisteredEvents(pageable));
    }

}

