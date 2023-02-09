package com.remindmeofthat.web;

import com.remindmeofthat.data.model.ReminderConfig;
import com.remindmeofthat.data.model.ReminderUser;
import com.remindmeofthat.data.repository.ReminderConfigRepository;
import com.remindmeofthat.data.repository.ReminderUserRepository;
import com.remindmeofthat.service.ReminderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
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

    private final ReminderConfigRepository reminderConfigRepository;

    private String linkId;
    private ReminderUser reminderUser;

    private ReminderConfig reminderConfig; // The current reminder config being worked on

    //Form fields that we want to bind and validate
    private BeanValidationBinder<ReminderConfig> binder = new BeanValidationBinder<>(ReminderConfig.class);

    Dialog editReminderConfigDialog = new Dialog();
    TextField subject = new TextField("Subject");
    TextArea body = new TextArea("Body");

    Checkbox recurring = new Checkbox();

    DatePicker reminderDate = new DatePicker("Reminder Date");

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    Button delete = new Button(new Icon(VaadinIcon.TRASH));

    public EditReminderView(@Autowired ReminderService reminderService, @Autowired ReminderUserRepository reminderUserRepository,
                            @Autowired ReminderConfigRepository reminderConfigRepository) {
        this.reminderService = reminderService;
        this.reminderUserRepository = reminderUserRepository;
        this.reminderConfigRepository = reminderConfigRepository;
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

        //Complete the bean validation binder setup
        binder.bindInstanceFields(this);

        //Create the popup dialog
        createDialog();

        //Add the add reminder button
        var addReminderButton = new Button("Add Reminder", new Icon(VaadinIcon.PLUS));
        addReminderButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        addReminderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.setHorizontalComponentAlignment(Alignment.CENTER, addReminderButton);
        addReminderButton.addClickListener(event -> {
            //TODO check whether we are editing or adding but for now just add a new one
            reminderConfig = new ReminderConfig();
            editReminderConfigDialog.open();
        });

        add(addReminderButton, editReminderConfigDialog);
    }

    private void createDialog() {
        //Make sure you can resize the dialog
        editReminderConfigDialog.setResizable(true);

        VerticalLayout dialogLayout = new VerticalLayout();

        subject.setWidth("28em");
        body.setWidth("32em");
        body.setHeight("250px");

        //Make this field resizable
        body.getStyle().set("resize", "both");
        body.getStyle().set("overflow", "auto");

        recurring.setLabel("Repeats");

        dialogLayout.add(subject, body, recurring, reminderDate, createDialogButtons());
        editReminderConfigDialog.add(dialogLayout);
    }

    private HorizontalLayout createDialogButtons() {
        HorizontalLayout buttonActions = new HorizontalLayout();

        //Put delete over on the right side
        buttonActions.setSizeFull();
        delete.getStyle().set("margin-left", "auto");

        //Cancel action
        cancel.addClickListener(event -> {
            binder.readBean(null);
            editReminderConfigDialog.close();
        });

        //Save action
        save.addClickListener(event -> {
            try {

                //Write this back to the binder object
                binder.writeBean(reminderConfig);

                logger.debug("Saving reminder config [{}]", reminderConfig);

                //Save this to the database
                //reminderConfigRepository.save(reminderConfig);

                //Close the dialog and update the items in the list
                editReminderConfigDialog.close();

            } catch (ValidationException e) {

                //TODO make sure the validation errors are handled sensibly for clients
                logger.warn("Validation error saving environment form");
            }
        });

        buttonActions.add(save, cancel, delete);

        return buttonActions;
    }
}
