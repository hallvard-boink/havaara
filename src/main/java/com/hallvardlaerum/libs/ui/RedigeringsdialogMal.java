package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.data.binder.Binder;

public class RedigeringsdialogMal<Entitet extends EntitetAktig> extends Dialog implements RedigeringsdialogAktig<Entitet> {
    EntitetserviceAktig<Entitet> entitetservice;
    RedigeringsomraadeMal<Entitet> redigeringsomraade;

    Button lagreIDialogButton;
    Button avbrytIDialogButton;

    public RedigeringsdialogMal(EntitetserviceAktig<Entitet> entitetservice) {
        this.entitetservice = entitetservice;
        if (entitetservice.hentRedigeringsomraadeAktig() instanceof RedigeringsomraadeMal<?>) {
            redigeringsomraade = (RedigeringsomraadeMal)entitetservice.hentRedigeringsomraadeAktig();
        } else {
            Loggekyklop.hent().loggFEIL("Redigeringsområdet var ikke av type redigeringsomraademal<Entitet>. Merkelig! Ingen dialogfunksjonalitet, altså.");
            return;
        }

        opprettDialog();
    }



    @Override
    public void visDialog(Entitet entitet){
        setHeaderTitle("Rediger " + entitet.hentBeskrivendeNavn());
        redigeringsomraade.setEntitet(entitet);
        redigeringsomraade.lesBean();
        this.open();
    }

    private void opprettDialog(){
        lagreIDialogButton = new Button("Lagre");
        lagreIDialogButton.addClickListener(e -> lagreIDialog());
        lagreIDialogButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        avbrytIDialogButton = new Button("Avbryt");
        avbrytIDialogButton.addClickListener(e ->  this.close());

        add(redigeringsomraade);
        add(lagreIDialogButton, avbrytIDialogButton);
        setModal(true);
    }

    private void lagreIDialog() {
        redigeringsomraade.skrivBean();
        entitetservice.lagre(redigeringsomraade.getEntitet());
        close();
    }


}
