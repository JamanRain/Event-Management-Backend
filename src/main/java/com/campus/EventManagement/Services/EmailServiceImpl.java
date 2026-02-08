package com.campus.EventManagement.Services;

import com.campus.EventManagement.Entities.Event;
import com.campus.EventManagement.Entities.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
    @Async
    @Override
    public void sendEventUpdatedMail(User user, Event event) {
        sendSimpleMail(
                user.getEmail(),
                "Event Updated: " + event.getTitle(),
                "Hi " + user.getName() + ",\n\n" +
                        "The event you registered for has been UPDATED:\n\n" +
                        "Event: " + event.getTitle() + "\n" +
                        "Please check the app for latest details.\n\n" +
                        "— EMS Team"
        );
    }

    @Async
    @Override
    public void sendEventDeletedMail(User user, Event event) {
        sendSimpleMail(
                user.getEmail(),
                "Event Cancelled: " + event.getTitle(),
                "Hi " + user.getName() + ",\n\n" +
                        "Unfortunately, the event you registered for has been CANCELLED:\n\n" +
                        "Event: " + event.getTitle() + "\n\n" +
                        "Sorry for the inconvenience.\n\n" +
                        "— EMS Team"
        );
    }

    @Async
    @Override
    public void sendEventApprovedMail(User club, Event event) {
        sendSimpleMail(
                club.getEmail(),
                "Your Event is Approved 🎉",
                "Hi " + club.getName() + ",\n\n" +
                        "Your event has been APPROVED by admin:\n\n" +
                        "Event: " + event.getTitle() + "\n\n" +
                        "It is now visible to students.\n\n" +
                        "— EMS Team"
        );
    }
    @Async
    @Override
    public void sendPasswordResetMail(User user, String token) {
        String link = "http://localhost:8082/api/v1/auth/reset-password?token=" + token;

        sendSimpleMail(
                user.getEmail(),
                "Reset Your Password",
                "Hi " + user.getName() + ",\n\n" +
                        "Click the link below to reset your password:\n" +
                        link + "\n\n" +
                        "This link is valid for 15 minutes.\n\n" +
                        "If you did not request this, ignore this email.\n\n" +
                        "— EMS Team"
        );
    }


}
