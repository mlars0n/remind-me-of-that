package com.remindmeofthat.web;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = ParentLayoutView.class)
@PageTitle("Home")
public class HomeView extends VerticalLayout {

    public HomeView() {

        Paragraph paragraph = new Paragraph(new Text("Welcome to Remind Me Of That."));

        add(paragraph);

    }
}
