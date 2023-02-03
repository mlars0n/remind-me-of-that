package com.remindmeofthat.web;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

/**
 * Establish the top menu
 */
/*@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")*/
public class ParentLayoutView extends AppLayout {

    public ParentLayoutView() {
        //Whether the navbar or drawer is primary
        setPrimarySection(Section.NAVBAR);

        addToNavbar(new Span("RemindMeOfThat"));

        RouterLink apiDataRouterLink = new RouterLink("Home", HomeView.class);
        RouterLink apiDataRouterLink2 = new RouterLink("About Us", AboutUs.class);

        Tab apiDataViewTab = new Tab(apiDataRouterLink);
        Tab apiDataViewTab2 = new Tab(apiDataRouterLink2);

        Tabs tabs = new Tabs(apiDataViewTab, apiDataViewTab2);
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        addToNavbar(tabs);
    }
}
