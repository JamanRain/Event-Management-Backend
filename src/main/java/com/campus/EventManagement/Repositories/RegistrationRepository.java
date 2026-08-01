package com.campus.EventManagement.Repositories;

import com.campus.EventManagement.Entities.Event;
import com.campus.EventManagement.Entities.Registration;
import com.campus.EventManagement.Entities.RegistrationStatus;
import com.campus.EventManagement.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository
        extends JpaRepository<Registration, Integer> {

    Optional<Registration> findByUserAndEvent(
            User user,
            Event event
    );

    Page<Registration> findByUser(
            User user,
            Pageable pageable
    );

    Page<Registration> findByEvent(
            Event event,
            Pageable pageable
    );

    Page<Registration> findByUserId(
            Long userId,
            Pageable pageable
    );

    Optional<Registration> findById(
            Long registrationId
    );

    List<Registration> findByStatus(
            RegistrationStatus status
    );

    Page<Registration> findByEventAndStatus(
            Event event,
            RegistrationStatus status,
            Pageable pageable
    );

    Long countByEvent(Event event);

    long count();
}