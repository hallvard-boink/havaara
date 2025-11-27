package com.hallvardlaerum.libs.feiloglogging;

import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;



public class HallvardsErrorHandler implements ErrorHandler {

    @Override
    public void error(ErrorEvent errorEvent) {
        try {
            throw errorEvent.getThrowable();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        //Loggekyklop.hent().loggFEIL(lagMelding(errorEvent));
    }

    private String lagMelding(ErrorEvent errorEvent) {
        if (errorEvent==null || errorEvent.getThrowable()==null) {
            return "Feil, men errorEvent eller getThrowable er null";
        } else {
            if (errorEvent.getThrowable().getMessage() != null) {
                return "Feilen er:" + errorEvent.getThrowable().getMessage();
            } else {
                return "Ukjent feil";
            }
        }
    }
}
