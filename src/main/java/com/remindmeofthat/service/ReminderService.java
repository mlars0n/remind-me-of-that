package com.remindmeofthat.service;

import com.remindmeofthat.data.model.ReminderUser;
import com.remindmeofthat.data.repository.ReminderUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Service to handle reminder creation and other reminder services
 */
@Service
public class ReminderService {

    private ReminderUserRepository reminderUserRepository;

    public ReminderService(@Autowired ReminderUserRepository reminderUserRepository) {
        this.reminderUserRepository = reminderUserRepository;
    }

    public ReminderUser createUser(String email, String givenName, String familyName) {

        //Create the basic user info
        ReminderUser reminderUser = new ReminderUser();
        reminderUser.setEmail(email);
        reminderUser.setGivenName(givenName);
        reminderUser.setFamilyName(familyName);

        //Add the link ID using a UUID
        UUID uuid = UUID.randomUUID();
        reminderUser.setLinkId(uuid.toString());
        reminderUser.setLinkCreatedDate(OffsetDateTime.now());

        //Save this new entity
        reminderUserRepository.save(reminderUser);

        return reminderUser;
    }
}