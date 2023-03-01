package com.remindmeofthat.data.repository;

import com.remindmeofthat.data.model.ReminderConfig;
import com.remindmeofthat.data.model.ReminderUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderConfigRepository extends JpaRepository<ReminderConfig, Long> {
    List<ReminderConfig> findReminderConfigByReminderUserOrderByCreatedDateDesc(ReminderUser reminderUser);
}
