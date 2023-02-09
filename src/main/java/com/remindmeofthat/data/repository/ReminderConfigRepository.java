package com.remindmeofthat.data.repository;

import com.remindmeofthat.data.model.ReminderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderConfigRepository extends JpaRepository<ReminderConfig, Long> {

}
