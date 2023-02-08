package com.remindmeofthat.data.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "reminder_config")
public class ReminderConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name = "recurring")
    private boolean recurring;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ReminderUser user;

    @OneToMany(mappedBy="reminderConfig")
    private Set<Reminder> reminders;

    // Getters and setters for the fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public ReminderUser getUser() {
        return user;
    }

    public void setUser(ReminderUser user) {
        this.user = user;
    }
}



