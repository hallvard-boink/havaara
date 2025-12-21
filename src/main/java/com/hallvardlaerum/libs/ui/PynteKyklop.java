package com.hallvardlaerum.libs.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.theme.lumo.LumoUtility;


public class PynteKyklop {
    private static PynteKyklop pynteKyklop;


    public void storeBlaaBokstaver(Component component) {
        component.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.PRIMARY);
    }


    private PynteKyklop() {
    }

    public static PynteKyklop hent(){
        if (pynteKyklop==null) {
            pynteKyklop = new PynteKyklop();
        }
        return pynteKyklop;
    }
}
