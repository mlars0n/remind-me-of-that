package com.remindmeofthat.web;

import com.remindmeofthat.data.model.ReminderUser;
import com.remindmeofthat.service.ReminderService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "", layout = ParentLayoutView.class)
@PageTitle("Home")
public class HomeView extends VerticalLayout {

    private static Logger logger = LoggerFactory.getLogger(HomeView.class);

    ReminderService reminderService;

    //Layout and page items
    VerticalLayout textVerticalLayout = new VerticalLayout();

    //Add form fields that we want to hand globally
    TextField collectEmail = new TextField("Email");
    Button submitButton = new Button("Submit", this::emailButtonListener);

    public HomeView(@Autowired ReminderService reminderService) {
        this.reminderService = reminderService;
        completeSetup();
    }

    private void completeSetup() {

        //Main part of the page layout
        HorizontalLayout mainHorizontalLayout = new HorizontalLayout();
        Image image = new Image("images/alarm_clock.jpeg", "Alarm clock banner");

        H1 greetingText = new H1("Welcome to Remind Me Of That!");
        H3 instructionText = new H3("To get started, enter your email. We'll email you a link that you can use to set up your first reminder.");

        //Email and button layout
        HorizontalLayout emailLayout = new HorizontalLayout();
        emailLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        emailLayout.add(collectEmail, submitButton);

        //Right column vertical layout
        textVerticalLayout.add(greetingText, instructionText, emailLayout);

        mainHorizontalLayout.add(image, textVerticalLayout);

        add(mainHorizontalLayout);
    }

    private void emailButtonListener(ClickEvent<Button> event) {
        //TODO find the user by email (maybe add an index there) and either create or retrieve the record and the link
        var reminderUser = reminderService.createUser(collectEmail.getValue(), null, null);
        logger.debug("User with email [{}] created", reminderUser.getEmail());

        //Create the link and display it for the user
        Anchor anchor = new Anchor("/edit/" + reminderUser.getLinkId(), "Create your reminders!");

        //Now update the vertical layout

        textVerticalLayout.add(anchor);

    }
}
