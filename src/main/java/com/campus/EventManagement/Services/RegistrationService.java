package com.campus.EventManagement.Services;

import com.campus.EventManagement.Entities.*;
import com.campus.EventManagement.Exceptions.BadRequestException;
import com.campus.EventManagement.Exceptions.ResourceNotFoundException;
import com.campus.EventManagement.Exceptions.UnauthorizedException;
import com.campus.EventManagement.Repositories.EventRepository;
import com.campus.EventManagement.Repositories.RegistrationRepository;
import com.campus.EventManagement.Repositories.UserRepository;
import com.campus.EventManagement.Security.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Optional<Registration> registerForEvent(Long eventId) {

        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Event not found"));

        if (user.getRole() == Role.CLUB) {
            throw new BadRequestException("Clubs cannot register for events");
        }

        if (event.getCreatedBy().getId().equals(userId)) {
            throw new BadRequestException("You cannot register for your own event");
        }

        if (!event.isApproved()) {
            throw new BadRequestException("Event is not approved yet");
        }

        if (event.getRegistered() >= event.getCapacity()) {
            throw new BadRequestException("Capacity full for this event");
        }

        if (registrationRepository.findByUserAndEvent(user, event).isPresent()) {
            throw new BadRequestException("You are already registered for this event");
        }

        Registration registration = new Registration();

        registration.setUser(user);
        registration.setEvent(event);
        registration.setRegisteredAt(LocalDateTime.now());

        registration.setStatus(
                RegistrationStatus.PENDING
        );

        event.setRegistered(
                event.getRegistered() + 1
        );

        eventRepository.save(event);

        try {

            emailService.sendSimpleMail(
                    user.getEmail(),
                    "Event Registration Confirmed ✅",
                    "Hi " + user.getName()
                            + ",\n\nYou have successfully registered for "
                            + event.getTitle()
                            + ".\n\nYour registration is currently PENDING approval from the club."
            );

        } catch (Exception ignored) {
        }

        return Optional.of(
                registrationRepository.save(registration)
        );
    }

    @Transactional
    public Optional<Boolean> cancelRegistration(Long eventId) {

        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Event not found"));

        Registration registration =
                registrationRepository
                        .findByUserAndEvent(user, event)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Registration not found"));

        registrationRepository.delete(registration);

        event.setRegistered(
                event.getRegistered() - 1
        );

        eventRepository.save(event);

        return Optional.of(true);
    }

    public Page<Event> getMyRegisteredEvents(Pageable pageable) {

        Long userId = SecurityUtil.getCurrentUserId();

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return registrationRepository
                .findByUserId(userId, pageable)
                .map(Registration::getEvent);
    }

    public Page<Registration> getEventRegistrations(
            Long eventId,
            Pageable pageable) {

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Event not found"));

        Long currentUser = SecurityUtil.getCurrentUserId();

        if (!event.getCreatedBy().getId().equals(currentUser)) {
            throw new UnauthorizedException("You don't own this event");
        }

        return registrationRepository.findByEvent(
                event,
                pageable
        );
    }

    @Transactional
    public Registration approveRegistration(Long registrationId) {

        Registration registration =
                registrationRepository.findById(registrationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Registration not found"));

        Long currentUser = SecurityUtil.getCurrentUserId();

        if (!registration.getEvent()
                .getCreatedBy()
                .getId()
                .equals(currentUser)) {

            throw new UnauthorizedException("You don't own this event.");
        }

        registration.setStatus(
                RegistrationStatus.APPROVED
        );

        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration rejectRegistration(Long registrationId) {

        Registration registration =
                registrationRepository.findById(registrationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Registration not found"));

        Long currentUser = SecurityUtil.getCurrentUserId();

        if (!registration.getEvent()
                .getCreatedBy()
                .getId()
                .equals(currentUser)) {

            throw new UnauthorizedException("You don't own this event.");
        }

        registration.setStatus(
                RegistrationStatus.REJECTED
        );

        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration revertRegistration(Long registrationId) {

        Registration registration =
                registrationRepository.findById(registrationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Registration not found"));

        Long currentUser = SecurityUtil.getCurrentUserId();

        if (!registration.getEvent()
                .getCreatedBy()
                .getId()
                .equals(currentUser)) {

            throw new UnauthorizedException("You don't own this event.");
        }

        registration.setStatus(
                RegistrationStatus.PENDING
        );

        return registrationRepository.save(registration);
    }
}