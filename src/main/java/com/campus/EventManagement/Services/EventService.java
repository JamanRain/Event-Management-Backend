package com.campus.EventManagement.Services;
import com.campus.EventManagement.Entities.Event;
import com.campus.EventManagement.Entities.Registration;
import com.campus.EventManagement.Entities.Role;
import com.campus.EventManagement.Entities.User;
import com.campus.EventManagement.Exceptions.BadRequestException;
import com.campus.EventManagement.Exceptions.ResourceNotFoundException;
import com.campus.EventManagement.Exceptions.UnauthorizedException;
import com.campus.EventManagement.Repositories.EventRepository;
import com.campus.EventManagement.Repositories.RegistrationRepository;
import com.campus.EventManagement.Repositories.UserRepository;
import com.campus.EventManagement.Security.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.nio.channels.ScatteringByteChannel;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private EmailService emailService;
    private static final Logger logger = Logger.getLogger(EventService.class.getName());

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

    public Optional<Event> updateEvent(Event updated, Long eventId, Pageable pageable) {

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

        Event saved = eventRepository.save(event);

        // 🔁 Iterate over all pages
        Page<Registration> page;
        Pageable currentPageable = pageable;

        do {
            page = registrationRepository.findByEvent(event, currentPageable);

            for (Registration registration : page.getContent()) {
                try {
                    emailService.sendEventUpdatedMail(registration.getUser(), saved);
                } catch (Exception e) {
                    logger.warning("Failed to send event updated mail to "
                            + registration.getUser().getEmail() + " : " + e.getMessage());
                }
            }

            currentPageable = page.nextPageable();

        } while (page.hasNext());

        return Optional.of(saved);
    }


    public Optional<Boolean> deleteEvent(Long eventId, Pageable pageable) {

        Long userId = SecurityUtil.getCurrentUserId();
        String role = SecurityUtil.getCurrentRole();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!role.contains("ADMIN")
                && !event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("Only ADMIN or creator club can delete this event");
        }

        Page<Registration> page;
        Pageable currentPageable = pageable;

        do {
            page = registrationRepository.findByEvent(event, currentPageable);
            logger.info("Processing page " + page.getNumber() + " with " + page.getNumberOfElements() + " registrations");

            for (Registration registration : page.getContent()) {
                try {
                    emailService.sendEventDeletedMail(registration.getUser(), event);
                    logger.info("Mail sent to " + registration.getUser().getEmail());
                } catch (Exception e) {
                    logger.warning("Failed to send mail to " + registration.getUser().getEmail()
                            + " reason: " + e.getMessage());
                }
            }

            currentPageable = page.nextPageable();

        } while (page.hasNext());

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
        Event saved = eventRepository.save(event);
        User club = saved.getCreatedBy();

        try
        {
            emailService.sendEventApprovedMail(club, saved);
        }
        catch (Exception e) {
            logger.warning("Failed to send approval mail");
        }
        return saved;
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

