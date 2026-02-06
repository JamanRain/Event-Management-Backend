package com.campus.EventManagement.Services;

import com.campus.EventManagement.Dto.UserResponse;
import com.campus.EventManagement.Entities.Event;
import com.campus.EventManagement.Entities.Registration;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Exceptions.BadRequestException;
import com.campus.EventManagement.Exceptions.ResourceNotFoundException;
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
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public Optional<Registration> registerForEvent(Long eventId) {

        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId).orElseThrow(() ->new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->new ResourceNotFoundException("Event not found"));

        if (!event.isApproved())
        {
            throw new BadRequestException("Event is not approved yet");
        }
        if (event.getRegistered() >= event.getCapacity())
            throw new BadRequestException("Capacity full for this event");

        if (registrationRepository.findByUserAndEvent(user, event).isPresent())
            throw new BadRequestException("You are already registered for this event");

        Registration reg = new Registration();
        reg.setUser(user);
        reg.setEvent(event);
        reg.setRegisteredAt(LocalDateTime.now());

        event.setRegistered(event.getRegistered() + 1);
        eventRepository.save(event);

        return Optional.of(registrationRepository.save(reg));
    }

    @Transactional
    public Optional<Boolean> cancelRegistration(Long eventId) {

        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId).orElseThrow(() ->new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->new ResourceNotFoundException("Event not found"));

        Registration reg =
                registrationRepository.findByUserAndEvent(user, event)
                        .orElseThrow(() -> new ResourceNotFoundException("Registration not found for this user"));

        registrationRepository.delete(reg);

        event.setRegistered(event.getRegistered() - 1);
        eventRepository.save(event);

        return Optional.of(true);
    }
    public Page<Event> getMyRegisteredEvents(Pageable pageable)
    {
        Long userId = SecurityUtil.getCurrentUserId();
        if(userRepository.findById(userId).isEmpty())
        {
            throw new ResourceNotFoundException("User Not Found");
        }

        Page<Registration> regs = registrationRepository.findByUserId(userId, pageable);

        return regs.map(Registration::getEvent);
    }
}
