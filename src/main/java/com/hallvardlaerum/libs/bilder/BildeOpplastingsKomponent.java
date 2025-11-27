package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.filerogopplasting.*;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;

/**
 * Funksjonalitet for å last opp og evt komprimere ett og ett bilde.
 *
 */
public class BildeOpplastingsKomponent extends VerticalLayout implements BildeOpplastingsKomponentAktig{
    private LastOppFilDialog lastOppFilDialog = null;
    private BildeVisningsKomponent bildeVisningsKomponent;
    private RedigeringsomraadeAktig<EntitetAktig> redigeringsomraade;
    private FilopplastingsEgnet filopplastingsEgnet;
    private HorizontalLayout knappeRadHorizontalLayout;
    private EntitetserviceAktig<BildeentitetAktig> bildeentitetservice;
    private AbstraktBildeentitet bildeentitet;

    /**
     * Enkel utgave - ment for å inkluderes i et redigeringsområde hvor entiteten har et tekstfelt for filnavn (implementerer filopplastingsegnet)
     * @param redigeringsomraade
     * @param redigeringsomraadeSomfilopplastingsEgnet
     */
    public BildeOpplastingsKomponent(RedigeringsomraadeAktig<EntitetAktig> redigeringsomraade,
                                     FilopplastingsEgnet redigeringsomraadeSomfilopplastingsEgnet) {
        this.redigeringsomraade = redigeringsomraade;
        this.filopplastingsEgnet = redigeringsomraadeSomfilopplastingsEgnet;

        opprettLayout();
    }

    /**
     * Avansert utgave - ment for å være del av Bilderedigeringsområde i MultiBildeKomponent
     * @param bilderedigeringsomraade
     * @param redigeringsomraadeSomfilopplastingsEgnet
     */
    public BildeOpplastingsKomponent(BildeRedigeringsomraadeAktig<BildeentitetAktig<EntitetAktig>, EntitetAktig> bilderedigeringsomraade,
                                     FilopplastingsEgnet redigeringsomraadeSomfilopplastingsEgnet) {

        this.redigeringsomraade = new BildeTilRedigeringsomraadeAdapter(bilderedigeringsomraade);
        this.filopplastingsEgnet = redigeringsomraadeSomfilopplastingsEgnet;

        opprettLayout();
    }





    @Override
    public HorizontalLayout hentKnappeRad(){
        return knappeRadHorizontalLayout;
    }

    private void opprettLayout(){
        this.setPadding(false);
        this.setMargin(false);

        knappeRadHorizontalLayout = new HorizontalLayout();
        add(knappeRadHorizontalLayout);

        opprettLayout_leggTilLastOppBildeButton();
        opprettLayout_leggTilVelgBildeButton();
        opprettLayout_leggTilKobleFraBildeButton();
        opprettLayout_leggTilSlettBildeButton();

        opprettLayout_leggTilBildeVisningsKomponent();
    }



    private void opprettLayout_leggTilBildeVisningsKomponent() {
        bildeVisningsKomponent = new BildeVisningsKomponent();
        add(bildeVisningsKomponent);
    }



    private void opprettLayout_leggTilSlettBildeButton() {
        Button slettBildeButton = new Button("Slett bilde fra server", LumoIcon.ERROR.create());
        slettBildeButton.addClickListener(e-> {
            visSlettBildeDialog();
        });
        knappeRadHorizontalLayout.add(slettBildeButton);
    }

    private void visSlettBildeDialog(){
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Slette bilde?");

        dialog.setText("Er du sikker på at du vil slette bildet '" + filopplastingsEgnet.getFilnavn() +
                "' som er koblet til " + redigeringsomraade.getEntitet().hentBeskrivendeNavn() + "?");
        dialog.setConfirmText("Ok, kjør på");
        dialog.setCancelText("Nei, avbryt");
        dialog.addConfirmListener(e -> slettBildeFraServer());
        dialog.open();
    }


    private void opprettLayout_leggTilKobleFraBildeButton() {
        Button kobleFraBildeButton = new Button("Koble fra bilde", LumoIcon.MINUS.create());
        kobleFraBildeButton.addClickListener(e -> kobleFraBildeOgLagre());
        knappeRadHorizontalLayout.add(kobleFraBildeButton);
    }

    private void opprettLayout_leggTilVelgBildeButton() {
        Button velgBildeButton = new Button("Velg allerede opplastet bilde", LumoIcon.SEARCH.create());
        velgBildeButton.addClickListener(e -> new VelgFilDialog(Filkategori.BILDE, filopplastingsEgnet,
                "Velg et av bildene på serveren for " + redigeringsomraade.getEntitet().hentBeskrivendeNavn()).open());
        knappeRadHorizontalLayout.add(velgBildeButton);
    }

    private void opprettLayout_leggTilLastOppBildeButton(){
        Button lastoppbildeButton = new Button("Last opp bilde", LumoIcon.UPLOAD.create());
        lastoppbildeButton.addClickListener(e -> {
            if (lastOppFilDialog == null) {
                lastOppFilDialog = new LastOppFilDialog(filopplastingsEgnet, Filkategori.BILDE);
            }
            lastOppFilDialog.vis(redigeringsomraade.getEntitet());
        });
        knappeRadHorizontalLayout.add(lastoppbildeButton);
    }


    private void slettBildeFraServer(){
        Filkyklop.hent().slettFil(filopplastingsEgnet.getFilnavn(), Filkategori.BILDE);
        filopplastingsEgnet.lagringEtterOpplastingEllerValgAvFil("");
        hentBildeFraFilnavn("");
    }

    private void kobleFraBildeOgLagre(){
        filopplastingsEgnet.lagringEtterOpplastingEllerValgAvFil("");
        hentBildeFraFilnavn("");
    }

    @Override
    public void byggOppBildeFraBytes(byte[] data) {
        bildeVisningsKomponent.byggOppBildeFraBytes(data);
    }

    @Override
    public void hentBildeFraFilnavn(String filnavnString) {
        bildeVisningsKomponent.hentBildeFraFilnavn(filnavnString);
    }



}
