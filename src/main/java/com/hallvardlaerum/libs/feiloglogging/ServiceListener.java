package com.hallvardlaerum.libs.feiloglogging;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;

/**
 * Denne brukes til å fange opp runtime errors i Spring Boot og vise dem til brukeren
 */
public class ServiceListener implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        /**
        serviceInitEvent.getSource().addSessionInitListener( initEvent -> {
            VaadinSession.getCurrent().setErrorHandler(new FeilJeger());
        });
         */

        serviceInitEvent.getSource().addUIInitListener( initEvent -> {
            VaadinSession.getCurrent().setErrorHandler(new HallvardsErrorHandler());
        });

    }
}
