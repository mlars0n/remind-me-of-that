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
    private ReminderUser reminderUser;

    @OneToMany(mappedBy="reminderConfig", cascade = {CascadeType.ALL, CascadeType.MERGE})
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

    public ReminderUser getReminderUser() {
        return reminderUser;
    }

    public void setReminderUser(ReminderUser reminderUser) {
        this.reminderUser = reminderUser;
    }

    public Set<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(Set<Reminder> reminders) {
        this.reminders = reminders;
    }

    @Override
    public String toString() {
        return "ReminderConfig{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", recurring=" + recurring +
                ", user=" + reminderUser +
                ", reminders=" + reminders +
                '}';
    }
}



