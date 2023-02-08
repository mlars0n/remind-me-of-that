package com.remindmeofthat.web;

import com.remindmeofthat.data.model.ReminderConfig;
import com.remindmeofthat.data.model.ReminderUser;
import com.remindmeofthat.data.repository.ReminderUserRepository;
import com.remindmeofthat.service.ReminderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "edit/:linkId", layout = ParentLayoutView.class)
@PageTitle("Home")
public class EditReminderView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger logger = LoggerFactory.getLogger(EditReminderView.class);

    private final ReminderService reminderService;
    private final ReminderUserRepository reminderUserRepository;

    private String linkId;
    private ReminderUser reminderUser;

    private ReminderConfig reminderConfig; // The current reminder config being worked on

    //Form fields that we want to bind and validate
    private BeanValidationBinder<ReminderConfig> binder = new BeanValidationBinder<>(ReminderConfig.class);
    TextField subject = new TextField("Subject");
    TextField body = new TextField("Body");

    RadioButtonGroup<Boolean> recurring = new RadioButtonGroup<>();

    DatePicker reminderDate = new DatePicker("Reminder Date");

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    Button delete = new Button(new Icon(VaadinIcon.TRASH));

    public EditReminderView(@Autowired ReminderService reminderService, @Autowired ReminderUserRepository reminderUserRepository) {
        this.reminderService = reminderService;
        this.reminderUserRepository = reminderUserRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        //TODO need all kinds of error checking here (format of the linkId, what it looks like, etc. to
        // prevent brute force type attacks)
        Optional<String> linkIdOptional = event.getRouteParameters().get("linkId");
        if (linkIdOptional.isPresent()) {
            linkId = linkIdOptional.get();
        }

        logger.debug("Edit ID parameter is [{}]", linkId);

        //TODO check on link ID expiration and disallow access if it is too old

        //Find the user record for this ID
        var reminderUserOptional = reminderUserRepository.findReminderUserByLinkId(linkId);

        //This user is found so display the edit page with any current reminders on it
        if (reminderUserOptional.isPresent()) {
            logger.debug("Reminder user is [{}]", reminderUserOptional);

            reminderUser = reminderUserOptional.get();

            completeSetup();
        } else {
            logger.debug("Reminder user could not be found");

            //TODO return a 404 page here long term
        }

    }



    private void completeSetup() {
       /* Paragraph paragraph = new Paragraph("Your link ID is " + linkId);
        add(paragraph);*/

        //Create the popup add/edit dialog
        FormLayout formLayout = new FormLayout();
        Dialog editReminderConfigPopup = new Dialog();
        recurring.setLabel("Recurring?");
        recurring.setItems(Boolean.TRUE, Boolean.FALSE);

        HorizontalLayout buttonLayout = createDialogButtons();
        formLayout.setColspan(buttonLayout, 2);
        formLayout.add(subject, body, recurring, reminderDate, createDialogButtons());
        editReminderConfigPopup.add(formLayout);


        //Add the add reminder button
        var addReminderButton = new Button("Add Reminder", new Icon(VaadinIcon.PLUS));
        addReminderButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        addReminderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.setHorizontalComponentAlignment(Alignment.CENTER, addReminderButton);
        addReminderButton.addClickListener(event -> {
            //Check whether we are editing or adding but for now just add a new one
            editReminderConfigPopup.open();
        });

        add(addReminderButton, editReminderConfigPopup);
    }

    private HorizontalLayout createDialogButtons() {
        HorizontalLayout buttonActions = new HorizontalLayout();
        buttonActions.add(save, cancel, delete);

        return buttonActions;
    }
}
