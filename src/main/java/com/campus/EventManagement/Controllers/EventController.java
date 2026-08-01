package com.campus.EventManagement.Controllers;

import com.campus.EventManagement.Entities.Event;
import com.campus.EventManagement.Entities.Role;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Security.SecurityUtil;
import com.campus.EventManagement.Services.EventService;
import com.campus.EventManagement.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('CLUB')")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @PutMapping("/update/{eventId}")
    @PreAuthorize("hasRole('CLUB')")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long eventId,
            @RequestBody Event event, Pageable pageable) {

        return eventService.updateEvent(event, eventId, pageable)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PutMapping("/approve/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.approveEvent(eventId));
    }

    @DeleteMapping("/delete/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN','CLUB')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId, Pageable pageable) {

        return eventService.deleteEvent(eventId, pageable)
                .map(r -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/search/approved")
    public ResponseEntity<Page<Event>> approved(Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllApprovedEvents(pageable));
    }

    @GetMapping("/my-club-events")
    public ResponseEntity<?> getMyClubEvents(Pageable pageable) {
        return ResponseEntity.ok(eventService.getMyClubEvents(pageable));
    }
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingEvents(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                eventService.getPendingEvents(
                        pageable
                )
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }
    @PutMapping("/revoke/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> revokeApproval(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                eventService.revokeApproval(eventId)
        );
    }

}
