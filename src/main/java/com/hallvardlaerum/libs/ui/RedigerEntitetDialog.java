package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class RedigerEntitetDialog<Entitet extends EntitetAktig, OmsluttendeEntitet extends EntitetAktig> extends Dialog{

    private RedigeringsomraadeMal<Entitet> redigeringsomraade;
    private RedigeringsomraadeMal<OmsluttendeEntitet> omsluttendeRedigeringsomraade;
    private EntitetserviceAktig<Entitet,?> entitetservice;
    private EntitetserviceAktig<OmsluttendeEntitet,?> omsluttendeEntitetservice;
    private Span beskrivelseSpan;
    private RedigerEntitetDialogEgnet<Entitet> redigerEntitetDialogEgnet;

    /**
     * Initiering uten redigerEntitetDialogEgnet
     * @param entitetservice Service til entiteten som skal redigeres
     * @param omsluttendeEntitetservice Service som brukes i redigeringsområdet dialogen starter fra. Brukes til oppdatering av forelder
     * @param strTittel Tittel på dialogboksen
     * @param strForklaring Forklaring i dialogbosken
     * @param redigeringsomraade Redigeringsområdet som skal vises i dialogen
     * @param omsluttendeRedigeringsomraade Redigeringsområdet som dialogen starter fra. Brukes til refresh
     */
    public RedigerEntitetDialog (EntitetserviceAktig<Entitet,?> entitetservice,
                                 EntitetserviceAktig<OmsluttendeEntitet,?> omsluttendeEntitetservice,
                                 String strTittel,
                                 String strForklaring,
                                 RedigeringsomraadeMal<Entitet> redigeringsomraade,
                                 RedigeringsomraadeMal<OmsluttendeEntitet> omsluttendeRedigeringsomraade){

        inititer(entitetservice,omsluttendeEntitetservice,strTittel,strForklaring,redigeringsomraade,omsluttendeRedigeringsomraade,null);
    }

    /**
     * Initiering med redigerEntitetDialogEgnet
     * @param entitetservice Service til entiteten som skal redigeres
     * @param omsluttendeEntitetservice Service som brukes i redigeringsområdet dialogen starter fra. Brukes til oppdatering av forelder
     * @param strTittel Tittel på dialogboksen
     * @param strForklaring Forklaring i dialogbosken
     * @param redigeringsomraade Redigeringsområdet som skal vises i dialogen
     * @param omsluttendeRedigeringsomraade Redigeringsområdet som dialogen starter fra. Brukes til refresh
     * @param redigerEntitetDialogEgnet et objekt som har implementert denne interfacen, for eksempel omsluttende redigeringsområde. Brukes til å kjøre oppdaterEtterLagring()
     */
    public RedigerEntitetDialog (EntitetserviceAktig<Entitet,?> entitetservice,
                                 EntitetserviceAktig<OmsluttendeEntitet,?> omsluttendeEntitetservice,
                                 String strTittel,
                                 String strForklaring,
                                 RedigeringsomraadeMal<Entitet> redigeringsomraade,
                                 RedigeringsomraadeMal<OmsluttendeEntitet> omsluttendeRedigeringsomraade,
                                 RedigerEntitetDialogEgnet<Entitet> redigerEntitetDialogEgnet) {
        inititer(entitetservice,omsluttendeEntitetservice,strTittel,strForklaring,redigeringsomraade,omsluttendeRedigeringsomraade,redigerEntitetDialogEgnet);
    }


    private void inititer(EntitetserviceAktig<Entitet,?> entitetservice,
                          EntitetserviceAktig<OmsluttendeEntitet,?> omsluttendeEntitetservice,
                          String strTittel,
                          String strForklaring,
                          RedigeringsomraadeMal<Entitet> redigeringsomraade,
                          RedigeringsomraadeMal<OmsluttendeEntitet> omsluttendeRedigeringsomraade,
                          RedigerEntitetDialogEgnet<Entitet> redigerEntitetDialogEgnet) {
        this.entitetservice = entitetservice;
        this.redigeringsomraade = redigeringsomraade;
        this.redigeringsomraade.setSizeFull();

        this.omsluttendeEntitetservice = omsluttendeEntitetservice;
        this.omsluttendeRedigeringsomraade = omsluttendeRedigeringsomraade;
        this.redigerEntitetDialogEgnet = redigerEntitetDialogEgnet;

        VerticalLayout verticalLayout = new VerticalLayout();
        this.add(verticalLayout);

        this.setHeaderTitle(strTittel);
        beskrivelseSpan = new Span(strForklaring);
        verticalLayout.add(beskrivelseSpan);
        verticalLayout.add(this.redigeringsomraade);
        verticalLayout.setSizeFull();
        verticalLayout.add(byggKnappeRad());

        this.setResizable(true);
        this.setDraggable(true);
        this.setWidth("90vw");
        this.setHeight("90vh");
    }

    public void settStoerrelse(Integer hoeydeiPixler, Integer breddeIPixler) {
        this.setWidth(breddeIPixler + "px");
        this.setHeight(hoeydeiPixler + "px");
    }


    public void vis(Entitet entitet, String tittelString, String beskrivelseString){
        redigeringsomraade.aktiver(true);
        redigeringsomraade.settEntitet(entitet);
        redigeringsomraade.lesBean();
        ((RedigeringsomraadeAktig<Entitet>) redigeringsomraade).instansOppdaterEkstraRedigeringsfelter();

        if (tittelString!=null && !tittelString.isEmpty()) {
            setHeaderTitle(tittelString);
        }
        if (beskrivelseString!=null && !beskrivelseString.isEmpty()) {
            beskrivelseSpan.setText(beskrivelseString);
        }
        redigeringsomraade.aktiver(true);
        redigeringsomraade.fokuser();
        open();

    }

    public void vis(Entitet entitet) {
        vis(entitet,null,null);
    }

    private HorizontalLayout byggKnappeRad(){
        HorizontalLayout knapperadHorizontalLayout = new HorizontalLayout();

        Button lagreButton = new Button("Lagre");
        lagreButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        lagreButton.addClickListener(e -> lagreEntitet());

        Button avbrytButton = new Button("Avbryt");
        avbrytButton.addClickListener(e -> this.close());

        knapperadHorizontalLayout.add(lagreButton, avbrytButton);
        return knapperadHorizontalLayout;
    }

    private void lagreEntitet() {
            redigeringsomraade.skrivBean();
            entitetservice.lagre(redigeringsomraade.hentEntitet());
            entitetservice.flush();
            omsluttendeEntitetservice.flush();
            this.close();

            if (redigerEntitetDialogEgnet!=null) {
                redigerEntitetDialogEgnet.oppdaterEtterLagring(redigeringsomraade.hentEntitet());
            }
            ((RedigeringsomraadeAktig<OmsluttendeEntitet>) omsluttendeRedigeringsomraade).instansOppdaterEkstraRedigeringsfelter();

    }



}
