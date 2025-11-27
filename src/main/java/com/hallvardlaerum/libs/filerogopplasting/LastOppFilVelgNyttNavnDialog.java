package com.hallvardlaerum.libs.filerogopplasting;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class LastOppFilVelgNyttNavnDialog extends Dialog {
    private LastOppFilDialogAktig hovedDialog;
    private HorizontalLayout knapperadHorizontalLayout;
    private String filnavnString;

    public LastOppFilVelgNyttNavnDialog(LastOppFilDialogAktig hovedDialog) {
        this.hovedDialog = hovedDialog;
        filnavnString = hovedDialog.getFilnavnString();
        opprettLayout();
    }

    private void opprettLayout(){
        opprettLayout_leggTilHeaderOgTekst();

        knapperadHorizontalLayout = new HorizontalLayout();
        add(knapperadHorizontalLayout);

        opprettLayout_leggTilBrukEksisterendeButton();
        opprettLayout_leggTilLagreMedNyttNavnButton();
        opprettLayout_leggTilAvbrytButton();
    }

    private void opprettLayout_leggTilHeaderOgTekst(){
        setHeaderTitle("Filen finnes fra før av");
        add(new Span("Filen " + filnavnString + " finnes fra før av på serveren. Vil du bruke den " +
                "eksisterende filen, eller lagre den nye filen med et nytt navn?"));
    }

    private void opprettLayout_leggTilBrukEksisterendeButton(){
        // Trykknapper
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button brukeEksisterendeButton = new Button("Bruke eksisterende");
        brukeEksisterendeButton.addClickListener(e -> {
            close();
            hovedDialog.close();  // lukk alle dialogbokser (det skal jo ikke skje en opplasting)
            hovedDialog.getView().lagringEtterOpplastingEllerValgAvFil(filnavnString);
        });
        knapperadHorizontalLayout.add(brukeEksisterendeButton);
    }

    private void opprettLayout_leggTilLagreMedNyttNavnButton(){
        Button lagreMedNyttNavnButton = new Button("Lagre med nytt navn");
        lagreMedNyttNavnButton.addClickListener(e -> {
            String nyttFilnavnString = Filkyklop.hent().opprettFilMedUniktNavn(hovedDialog.getFilnavnString(), hovedDialog.getFilkategori()).getName();
            hovedDialog.setFilnavnString(nyttFilnavnString);
            close();
            hovedDialog.oppdaterErUnik();
        });
        knapperadHorizontalLayout.add(lagreMedNyttNavnButton);
    }

    private void opprettLayout_leggTilAvbrytButton(){
        Button avbrytButton = new Button(("Avbryt"));
        avbrytButton.addClickListener( e -> {
            close();
        });
        knapperadHorizontalLayout.add(avbrytButton);
    }


}
