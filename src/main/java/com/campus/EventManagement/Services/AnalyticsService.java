package com.campus.EventManagement.Services;

import com.campus.EventManagement.Dto.AnalyticsResponse;
import com.campus.EventManagement.Entities.Role;
import com.campus.EventManagement.Repositories.EventRepository;
import com.campus.EventManagement.Repositories.RegistrationRepository;
import com.campus.EventManagement.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    public AnalyticsResponse getAnalytics() {

        long totalUsers =
                userRepository.count();

        long students =
                userRepository.countByRole(
                        Role.STUDENT
                );

        long clubs =
                userRepository.countByRole(
                        Role.CLUB
                );

        long totalEvents =
                eventRepository.count();

        long approved =
                eventRepository
                        .countByApproved(true);

        long pending =
                eventRepository
                        .countByApproved(false);

        long registrations =
                registrationRepository.count();

        return new AnalyticsResponse(
                totalUsers,
                students,
                clubs,
                totalEvents,
                approved,
                pending,
                registrations
        );
    }
}