package com.remindmeofthat.data.model;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "given_name", nullable = true)
    private String givenName;

    @Column(name = "family_name", nullable = true)
    private String familyName;

    @OneToMany(mappedBy="user")
    private Set<ReminderConfig> reminderConfigs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    public Set<ReminderConfig> getReminderConfigs() {
        return reminderConfigs;
    }

    public void setReminderConfigs(Set<ReminderConfig> reminderConfigs) {
        this.reminderConfigs = reminderConfigs;
    }
}

