package com.hallvardlaerum.libs.verktoy;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;

/**
 * Denne fungerer ikke optimalt. Filen copytoclipboard.js må ligge under "src/main/frontend" i hovedapplikasjonen.
 * Koden er som følger:
 *
 * window.copyToClipboard = (str) => {
 *   const textarea = document.createElement("textarea");
 *   textarea.value = str;
 *   textarea.style.position = "absolute";
 *   textarea.style.opacity = "0";
 *   document.body.appendChild(textarea);
 *   textarea.select();
 *   document.execCommand("copy");
 *   document.body.removeChild(textarea);
 * };
 *
 */

// Denne peker på /frontend i havaaratest, ikke havaara
@JsModule("./copytoclipboard.js")
public class Clipboardkyklop {
    private static Clipboardkyklop clipboardkyklop;


    public void kopierTekstTilClipboard(String tekst) {
        UI.getCurrent().getPage().executeJs("window.copyToClipboard($0)", tekst);
    }

    public static Clipboardkyklop hent(){
        if (clipboardkyklop == null ) {
            clipboardkyklop = new Clipboardkyklop();
        }
        return clipboardkyklop;
    }

    private Clipboardkyklop() {
    }
}

