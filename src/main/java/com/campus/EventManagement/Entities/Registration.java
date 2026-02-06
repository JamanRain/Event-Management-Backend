package com.campus.EventManagement.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "registration",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "event_id"})
        }
)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_registration_user")
    )
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "event_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_registration_event")
    )
    private Event event;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }




}
