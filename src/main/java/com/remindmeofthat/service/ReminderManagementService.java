package com.remindmeofthat.service;

import com.helger.commons.datetime.OffsetDate;
import com.remindmeofthat.data.model.Reminder;
import com.remindmeofthat.data.model.ReminderConfig;
import com.remindmeofthat.data.repository.ReminderConfigRepository;
import com.remindmeofthat.data.repository.ReminderRepository;
import com.remindmeofthat.data.repository.ReminderUserRepository;
import com.remindmeofthat.web.EditReminderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service to handle reminder creation and other reminder services
 */
@Service
public class ReminderManagementService {

    private static final Logger logger = LoggerFactory.getLogger(ReminderManagementService.class);

    private ReminderUserRepository reminderUserRepository;
    private ReminderConfigRepository reminderConfigRepository;
    private ReminderRepository reminderRepository;

    public ReminderManagementService(@Autowired ReminderConfigRepository reminderConfigRepository,
                                     @Autowired ReminderUserRepository reminderUserRepository,
                                     @Autowired ReminderRepository reminderRepository) {
        this.reminderConfigRepository = reminderConfigRepository;
        this.reminderUserRepository = reminderUserRepository;
        this.reminderRepository = reminderRepository;
    }

    /**
     * Method to encompass the logic that creates reservations
     * @param reminderConfig
     */
    @Transactional
    public void createRemindersWithRandomTime(ReminderConfig reminderConfig, LocalDate reminderStartDate, int timeZoneOffset) {

        //Create a recurring reminder if one is called for
        if (reminderConfig.getRecurring()) {
            //TODO this might need to be
            createRepeatingReminder(reminderConfig);
        } else { //Else just create a single reminder
            Reminder reminder = new Reminder();
            reminder.setSent(false);
            reminder.setReminderConfig(reminderConfig);

            //Create a localDateTime object with a time of 2:00am
            //TODO randomize the time as a time before 00:00 and 04:00
            LocalTime localTime = LocalTime.of(2, 0, 0);
            LocalDateTime localDateTime = LocalDateTime.of(reminderStartDate, localTime);

            // Convert the local date and time to an OffsetDateTime object with the correct time zone offset
            ZoneOffset zoneOffset = ZoneOffset.ofHours(timeZoneOffset);
            OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);

            //Set the time
            reminder.setReminderTime(offsetDateTime);

            //Set the reminder date (if there's only one reminder this is the same as the one reminder we are creating)f
            reminderConfig.setStartDate(offsetDateTime);

            logger.debug("Saving reminder config [{}] with reminder date/time of [{}]", reminderConfig, offsetDateTime);

            //Set the new reminder into the set
            Set<Reminder> reminderSet = new HashSet<>(Arrays.asList(reminder));
            reminderConfig.setReminders(reminderSet);

            //Save it all into the DB
            reminderConfigRepository.save(reminderConfig);
        }
    }

    public void createRepeatingReminder(ReminderConfig reminderConfig) {
        //TODO create repeating reminders
    }
}