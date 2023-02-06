package com.remindmeofthat.data.repository;

import com.remindmeofthat.data.model.ReminderUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReminderUserRepository extends JpaRepository<ReminderUser, Long> {
    public Optional<ReminderUser> findReminderUserByLinkId(String linkId);
}
