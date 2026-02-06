package com.campus.EventManagement.Repositories;

import com.campus.EventManagement.Entities.Event;
import com.campus.EventManagement.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    Optional<Event> findById(Long id);

    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Event> findByCreatedById(Long userId, Pageable pageable);

    Page<Event> findAllByApprovedTrue(Pageable pageable);
}
