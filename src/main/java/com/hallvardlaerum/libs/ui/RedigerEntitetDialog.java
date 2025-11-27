package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class RedigerEntitetDialog<BarneKlasse extends EntitetAktig, ForelderKlasse extends EntitetAktig> extends Dialog{

    private RedigeringsomraadeMal<BarneKlasse> barnRedigeringsomraade;
    private RedigeringsomraadeMal<ForelderKlasse> forelderRedigeringsomraade;
    private EntitetserviceAktig<BarneKlasse> barnEntitetservice;
    private EntitetserviceAktig<ForelderKlasse> forelderEntitetservice;


    public RedigerEntitetDialog (EntitetserviceAktig<BarneKlasse> barnEntitetservice,
                                 EntitetserviceAktig<ForelderKlasse> forelderEntitetservice,
                                 String strTittel,
                                 String strForklaring) {
        this.barnEntitetservice = barnEntitetservice;
        if (barnEntitetservice.hentRedigeringsomraadeAktig() instanceof RedigeringsomraadeMal<?>) {
            this.barnRedigeringsomraade = (RedigeringsomraadeMal<BarneKlasse>) barnEntitetservice.hentRedigeringsomraadeAktig();
        } else {
            Loggekyklop.hent().loggFEIL("Barneredigeringsområdet bruker ikke RedigeringsomraadeMal, avbryter");
            return;
        }

        this.forelderEntitetservice = forelderEntitetservice;
        if (forelderEntitetservice.hentRedigeringsomraadeAktig() instanceof RedigeringsomraadeMal<?>) {
            this.forelderRedigeringsomraade = (RedigeringsomraadeMal<ForelderKlasse>) forelderEntitetservice.hentRedigeringsomraadeAktig();
        } else {
            Loggekyklop.hent().loggFEIL("Foreldereredigeringsområdet bruker ikke RedigeringsomraadeMal, avbryter");
            return;
        }

        VerticalLayout verticalLayout = new VerticalLayout();
        this.add(verticalLayout);

        this.setHeaderTitle(strTittel);
        Span forklaringSpan = new Span(strForklaring);
        verticalLayout.add(forklaringSpan);
        verticalLayout.add(barnRedigeringsomraade);
        verticalLayout.setSizeFull();
        verticalLayout.add(byggKnappeRad());

    }


    public void vis(BarneKlasse entitet){
        barnRedigeringsomraade.aktiver(true);
        barnRedigeringsomraade.setEntitet(entitet);
        barnRedigeringsomraade.lesBean();
        barnRedigeringsomraade.fokuser();
        open();

    }

    private HorizontalLayout byggKnappeRad(){
        HorizontalLayout knapperadHorizontalLayout = new HorizontalLayout();

        Button lagreButton = new Button("Lagre");
        lagreButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        lagreButton.addClickListener(e -> lagreEntitet());

        Button avbrytButton = new Button("Avbryt");
        avbrytButton.addClickListener(e -> {this.close();});

        knapperadHorizontalLayout.add(lagreButton, avbrytButton);
        return knapperadHorizontalLayout;
    }

    private void lagreEntitet() {
            barnRedigeringsomraade.skrivBean();
            barnEntitetservice.lagre(barnRedigeringsomraade.getEntitet());
            barnEntitetservice.flush();
            forelderEntitetservice.flush();
            this.close();

            ((RedigeringsomraadeAktig<ForelderKlasse>)forelderRedigeringsomraade).instansOppdaterEkstraRedigeringsfelter();

    }



}
