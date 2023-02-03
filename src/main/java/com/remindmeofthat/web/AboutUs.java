package com.remindmeofthat.web;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "about", layout = ParentLayoutView.class)
@PageTitle("Home2")
public class AboutUs extends VerticalLayout {

    public AboutUs() {

        H1 greetingText = new H1("About Us");

        add(greetingText);

    }
}
