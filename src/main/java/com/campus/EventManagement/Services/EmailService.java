package com.campus.EventManagement.Services;

import com.campus.EventManagement.Entities.Event;
import com.campus.EventManagement.Entities.User;

public interface EmailService {
     void sendSimpleMail(String to, String subject, String body);
     void sendEventUpdatedMail(User user, Event event);
     void sendEventDeletedMail(User user, Event event);
     void sendEventApprovedMail(User club, Event event);

     void sendPasswordResetMail(User user, String token);


}

