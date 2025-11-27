package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class SlettEntitetDialog<T extends EntitetAktig> extends Dialog {

    private T entitet;
    private EntitetserviceAktig<T> entitetservice;
    private Span hovedomraadeSpan;
    private RedigeringsomraadeAktig<T> redigeringsomraadeAktig;

    public SlettEntitetDialog(EntitetserviceAktig<T> entitetservice) {
        this.entitetservice = entitetservice;
        this.redigeringsomraadeAktig = entitetservice.hentRedigeringsomraadeAktig();

        opprettLayout();

    }

    private void opprettLayout(){
        this.setHeaderTitle("Slett " + entitetservice.hentEntitetsnavn());

        hovedomraadeSpan = new Span();
        this.add(hovedomraadeSpan);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button lagreButton = new Button("Lagre");
        lagreButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        lagreButton.addClickListener(e -> slettEntitet());
        Button avbrytButton = new Button("Avbryt");
        avbrytButton.addClickListener(e -> {this.close();});
        horizontalLayout.add(lagreButton,avbrytButton);
        this.add(horizontalLayout);
    }

    private void slettEntitet(){
        entitetservice.slett(entitet);
        entitetservice.flush();
        this.close();
        redigeringsomraadeAktig.instansOppdaterEkstraRedigeringsfelter();
    }


    public void vis(T entitet) {
        this.entitet = entitet;
        hovedomraadeSpan.setText("Er du sikker på at du vil slette " + this.entitet.hentBeskrivendeNavn() + "?");
        this.open();
    }

}
