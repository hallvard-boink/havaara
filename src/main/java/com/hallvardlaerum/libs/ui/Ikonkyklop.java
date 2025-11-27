package com.hallvardlaerum.libs.ui;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class Ikonkyklop {
    private static Ikonkyklop ikonkyklop;


    public Icon opprettVaadinIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs)");
        return icon;
    }

    public static Ikonkyklop hent(){
        if (ikonkyklop == null) {
            ikonkyklop = new Ikonkyklop();
        }
        return ikonkyklop;
    }

    private Ikonkyklop() {
    }
}
