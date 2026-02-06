package com.campus.EventManagement.Services;
import com.campus.EventManagement.Entities.Event;
import com.campus.EventManagement.Entities.Role;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Exceptions.BadRequestException;
import com.campus.EventManagement.Exceptions.ResourceNotFoundException;
import com.campus.EventManagement.Exceptions.UnauthorizedException;
import com.campus.EventManagement.Repositories.EventRepository;
import com.campus.EventManagement.Repositories.UserRepository;
import com.campus.EventManagement.Security.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<Event> createEvent(Event event) {

        Long clubId = SecurityUtil.getCurrentUserId();

        User club = userRepository.findById(clubId).orElseThrow(() -> new ResourceNotFoundException("Creator user not found"));
        if(!SecurityUtil.getCurrentRole().contains("CLUB"))
        {
            throw new UnauthorizedException("Only CLUB can create events");
        }

        event.setCreatedBy(club);
        event.setApproved(false);

        return Optional.of(eventRepository.save(event));
    }

    public Optional<Event> updateEvent(Event updated, Long eventId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));


        if (!event.getCreatedBy().getId().equals(userId))
            throw new UnauthorizedException("Only the creator club can update this event");

        event.setTitle(updated.getTitle());
        event.setDescription(updated.getDescription());
        event.setEventDate(updated.getEventDate());
        event.setVenue(updated.getVenue());
        event.setCapacity(updated.getCapacity());

        return Optional.of(eventRepository.save(event));
    }

    public Optional<Boolean> deleteEvent(Long eventId) {

        Long userId = SecurityUtil.getCurrentUserId();
        String role = SecurityUtil.getCurrentRole();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));


        if (!role.contains("ADMIN")
                && !event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("Only ADMIN or creator club can delete this event");
        }


        eventRepository.delete(event);
        return Optional.of(true);
    }

    @Transactional
    public Event approveEvent(Long eventId) {

        Long userId = SecurityUtil.getCurrentUserId();
        String role = SecurityUtil.getCurrentRole();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!role.contains("ADMIN")) {

            if (event.getCreatedBy() == null) {
                throw new RuntimeException("Event has no creator assigned");
            }

            if (!event.getCreatedBy().getId().equals(userId)) {
                throw new UnauthorizedException("Only ADMIN or creator club can approve this event");
            }
        }

        event.setApproved(true);
        return eventRepository.save(event);
    }

    public Page<Event> getAllApprovedEvents(Pageable pageable) {
        return eventRepository.findAllByApprovedTrue(pageable);
    }

    public Page<Event> getMyClubEvents(Pageable pageable) {

        String role = SecurityUtil.getCurrentRole();
        Long clubId = SecurityUtil.getCurrentUserId();

        if (!role.contains("CLUB")) {
            throw new UnauthorizedException("Only clubs can access this");
        }
        return eventRepository.findByCreatedById(clubId, pageable);
    }
}

