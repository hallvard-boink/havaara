package com.hallvardlaerum.libs.filerogopplasting;

import com.hallvardlaerum.libs.bilder.Bildekyklop;
import com.hallvardlaerum.libs.bilder.KomprimerBildeDialog;
import com.hallvardlaerum.libs.database.EntitetAktig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;

import java.io.File;

public class LastOppFilDialog extends Dialog implements LastOppFilDialogAktig {
    private UploadMetadata opplastetfilMetadata;
    private byte[] opplastetfilData;
    private File nyLokalFilFile;
    private FilopplastingsEgnet view;
    private Filkategori filkategori;
    private Boolean erUnik;
    private Boolean erPasseStor;
    private Boolean skalKomprimeres = false;
    private HorizontalLayout knapperHorizontalLayout;
    private EntitetAktig entitet;
    private Button okButton;
    private Button endreNavnButton;
    private Button komprimerButton;
    private String filnavnString;
    private KomprimerBildeDialog komprimerBildeDialog;
    private LastOppFilVelgNyttNavnDialog lastOppFilVelgNyttNavnDialog;


    // TODO: Redesign disse dialogboksene. Prinsipper:
    // 1. LastOppFilDialog lagrer og tilgjengeliggjør originalLokaltFilnavnStreng, nyLokalFilFile, opplastetfilData_original, opplastetfilData_komprimert, abstraktentitet (for presentasjon)
    // 2. Andre dialogbokser endrer og tilpasser valg og filnavn i LastOppFilDialog. De åpnes og lukkes i tillegg til LastOppFilDialog.
    // 3. Ikke lagre på disk før bruker trykker OK lagre i denne dialogboksen.
    // 4. Hvis det både skal opprettes nytt navn og bildet skal komprimeres, åpnes begge dialogboksene (navn først)
    // 5. Funksjonalitet for komprimering av bilder flyttes til ny Bildekyklop, sammen med variablene
    // 6. Bruk filnavnString mest mulig



    public LastOppFilDialog(FilopplastingsEgnet view, Filkategori filkategori) {
        this.view = view;
        this.filkategori = filkategori;

        opprettLayout_leggTilUploadkomponent();
        opprettLayout_leggTilOkAvbrytButtons();
        opprettLayout_leggTilEndreNavnButton();
        opprettLayout_leggTilKomprimerFilButton();
    }

    public void vis(EntitetAktig entitet) {
        this.entitet = entitet;
        setHeaderTitle("Last opp " + filkategori.getVisningsNavn() + " for " + entitet.hentBeskrivendeNavn());

        open();
    }

    private void opprettLayout_leggTilOkAvbrytButtons(){
        knapperHorizontalLayout = new HorizontalLayout();

        okButton = new Button("Ok, lagre");
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        okButton.setEnabled(false);
        okButton.addClickListener(e -> {
            nyLokalFilFile = Filkyklop.hent().settSammenFile(filnavnString, Filkategori.BILDE);
            if (skalKomprimeres) {
                Bildekyklop.hent().komprimerOgLagreBilde(nyLokalFilFile);
                //komprimerBildeDialog.lagreOpplastetFilMedKomprimering();
            } else {
                Filkyklop.hent().skrivBytesTilFil(nyLokalFilFile, opplastetfilData);
            }
            view.lagringEtterOpplastingEllerValgAvFil(nyLokalFilFile.getName());
            close();
        });

        Button avbrytButton = new Button("Avbryt");
        avbrytButton.addClickListener(e -> close());

        knapperHorizontalLayout.add(okButton, avbrytButton);
        add(knapperHorizontalLayout);
    }

    private void opprettLayout_leggTilUploadkomponent(){
        InMemoryUploadHandler inMemoryUploadHandler = UploadHandler.inMemory( (metadata, data) -> {
            opplastetfilMetadata = metadata;
            opplastetfilData = data;
            filnavnString = opplastetfilMetadata.fileName();
            tilpassVisningAvButtons();
        });

        add(new Upload(inMemoryUploadHandler));
    }


    private void tilpassVisningAvButtons(){
        erPasseStor = Filkyklop.hent().sjekkAtFilenIkkeErForStor(opplastetfilMetadata);
        erUnik = Filkyklop.hent().sjekkAtFileIkkeFinnesAllerede(opplastetfilMetadata.fileName(),filkategori);

        endreNavnButton.setVisible(!erUnik);
        komprimerButton.setVisible(!erPasseStor);

        if (erUnik && (erPasseStor || skalKomprimeres)) {
            okButton.setEnabled(true);
        }
    }


    private void opprettLayout_leggTilKomprimerFilButton() {
        komprimerButton = new Button("Komprimer bilde");
        komprimerButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        komprimerButton.setTooltipText("Bildet er for stort");
        komprimerBildeDialog = new KomprimerBildeDialog(this);
        komprimerButton.addClickListener(e -> {
            komprimerBildeDialog.open();
        });
        knapperHorizontalLayout.add(komprimerButton);
    }

    private void opprettLayout_leggTilEndreNavnButton(){
        endreNavnButton = new Button("Endre filnavn eller velg eksisterende fil");
        endreNavnButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        endreNavnButton.setTooltipText("En fil med samme navn finnes fra før på serveren.");
        lastOppFilVelgNyttNavnDialog = new LastOppFilVelgNyttNavnDialog(this);
        endreNavnButton.addClickListener(e -> {
            lastOppFilVelgNyttNavnDialog.open();
        });
        knapperHorizontalLayout.add(endreNavnButton);
    }

    @Override
    public void oppdaterErUnik() {
        erUnik = Filkyklop.hent().sjekkAtFileIkkeFinnesAllerede(opplastetfilMetadata.fileName(),filkategori);
        if (erUnik) {
            endreNavnButton.setEnabled(false);
            endreNavnButton.removeThemeVariants(ButtonVariant.LUMO_ERROR);
            if (erPasseStor || skalKomprimeres) {
                okButton.setEnabled(true);
            }
        }
    }

    @Override
    public void oppdaterErPasseStor() {
        erPasseStor = Filkyklop.hent().sjekkAtFilenIkkeErForStor(opplastetfilMetadata);
        if (erPasseStor || skalKomprimeres) {
            komprimerButton.setEnabled(false);
            komprimerButton.removeThemeVariants(ButtonVariant.LUMO_ERROR);
            if (erUnik) {
                okButton.setEnabled(true);
            }
        }
    }

    @Override
    public UploadMetadata getOpplastetfilMetadata() {
        return opplastetfilMetadata;
    }

    @Override
    public byte[] getOpplastetfilData() {
        return opplastetfilData;
    }

    @Override
    public String getFilnavnString() {
        return filnavnString;
    }

    @Override
    public void setFilnavnString(String filnavnString) {
        this.filnavnString = filnavnString;
    }

    @Override
    public Filkategori getFilkategori() {
        return filkategori;
    }

    @Override
    public FilopplastingsEgnet getView() {
        return view;
    }

    public void settSkalKomprimeresOgOppdaterLayout(Boolean skalKomprimeres) {
        this.skalKomprimeres = skalKomprimeres;
        oppdaterErPasseStor();
    }
}
