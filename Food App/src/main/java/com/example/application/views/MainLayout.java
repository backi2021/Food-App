package com.example.application.views;

import java.util.Optional;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.user.Customer;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.service.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;

// Quelle: https://www.youtube.com/watch?v=oMKks5AjaSQ&ab_channel=vaadinofficial
//         https://www.youtube.com/watch?v=bxy2JgqqKDU&t=4757s&ab_channel=vaadinofficial
//         https://vaadin.com/docs/latest/flow/tutorials/in-depth-course/navigation-and-layouts

@PageTitle("Main")
@JsModule("./styles/shared-styles.js")
@CssImport("./views/main/main-view.css")
public class MainLayout extends AppLayout {

    private final Tabs menu;
    private H1 viewTitle;
    private AuthService authService;

    public MainLayout(AuthService authService) {
        this.authService = authService;
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        viewTitle = new H1();
        layout.add(viewTitle);
        return layout;
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new H1("Surpeme Eating Program"));
        layout.add(logoLayout, menu);
        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        return tabs;
    }

    private Component[] createMenuItems() {
        var user = VaadinSession.getCurrent().getAttribute(RestaurantOwner.class);
        var user1 = VaadinSession.getCurrent().getAttribute(Customer.class);

        if (user != null) {
            return authService.getAuthorizedRoutes(user.getRole()).stream()
                    .map(r -> createTab(r.name(), r.view()))
                    .toArray(Component[]::new);
        }
        else {
            return authService.getAuthorizedRoutes(user1.getRole()).stream()
                    .map(r -> createTab(r.name(), r.view()))
                    .toArray(Component[]::new);
        }
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren()
                .filter(tab -> ComponentUtil.getData(tab, Class.class)
                        .equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}

