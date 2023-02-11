package com.remindmeofthat.data.repository;

import com.remindmeofthat.data.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

}
