package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetMedBarnAktig;
import com.hallvardlaerum.libs.database.EntitetMedForelderAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Rekkefolgekyklop;
import com.hallvardlaerum.libs.filerogopplasting.Filkategori;
import com.hallvardlaerum.libs.filerogopplasting.FilopplastingsEgnet;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;


/**
 * Brukes til å redigere innholdet i AbstraktBildeEntitet.
 * Har egen valgt entitet, som må synkroniseres med valgtBilde i BildeOpplastingsKomponent
 *
 * @param <Bildeklasse>
 */
public class BildeRedigeringsomraadeMal<Bildeklasse extends BildeentitetAktig<Forelderklasse>, Forelderklasse extends EntitetAktig>
        extends RedigeringsomraadeMal<Bildeklasse>
        implements BildeRedigeringsomraadeAktig<Bildeklasse, Forelderklasse>, RedigeringsomraadeAktig<Bildeklasse> {

    private TextField filnavnTextField;
    private TextField tittelTextField;
    private NumberField stoerrelseIMbNumberField;
    private IntegerField hoeydeIPixlerIntegerField;
    private IntegerField breddeIPixlerIntegerField;
    private TextArea beskrivelseTextArea;
    private Checkbox erHovedbildeCheckbox;
    private IntegerField rekkefoelgeIntegerField;
    private BildeOpplastingsKomponent bildeOpplastingsKomponent;
    private EntitetserviceAktig<Bildeklasse> bildeService;
    private EntitetserviceAktig<Forelderklasse> forelderService;

    private ComboBox<Forelderklasse> forelderCombobox;
    private Boolean erFrittstaaende;

    public BildeRedigeringsomraadeMal() {
        super();
    }




    @Override
    public void initier(EntitetserviceAktig<Bildeklasse> bildeservice, EntitetserviceAktig<Forelderklasse> forelderService, Boolean erFrittstaaende) {
        this.bildeService = bildeservice;
        this.forelderService = forelderService;
        this.erFrittstaaende = erFrittstaaende;

        if (filnavnTextField==null) {
            instansOpprettFelter();
            instansByggOppBinder();
            if (this.erFrittstaaende) {
                visForelderCombobox();
            }
            Loggekyklop.hent().loggINFO("Bilderedigeringsomraade ble initiert");
        }
    }


    public void visForelderCombobox(){
        forelderCombobox = super.leggTilRedigeringsfelt(new ComboBox<>("Forelder"));
        forelderCombobox.setItems(forelderService.finnAlle());
        forelderCombobox.setItemLabelGenerator(EntitetAktig::hentBeskrivendeNavn);
        super.hentBinder().bind(forelderCombobox, Bildeklasse::getForelder, Bildeklasse::setForelder);
    }


    @Override
    public void setBildeentitet(BildeentitetAktig bildeentitet) {
        super.setEntitet((Bildeklasse) bildeentitet);
    }


    @Override
    public Bildeklasse getBildeentitet() {
        return super.getEntitet();
    }

    @Override
    public Binder<Bildeklasse> hentBinder() {
        return super.hentBinder();
    }

    @Override
    public Bildeklasse getEntitet() {
        return super.getEntitet();
    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        if (bildeOpplastingsKomponent==null) {
            Loggekyklop.hent().loggFEIL("BildeopplastingsKomponent er null. iiik! Noe er feil med initieringen av komponenter.");
            return;
        }

        if (getEntitet()!=null) {
            bildeOpplastingsKomponent.hentBildeFraFilnavn(getEntitet().getFilnavn());

        } else {
            bildeOpplastingsKomponent.hentBildeFraFilnavn("");
        }
    }


    private RedigeringsomraadeAktig<EntitetAktig> visSomRedigeringsomraade(){
        BildeTilRedigeringsomraadeAdapter<Bildeklasse, Forelderklasse> adapter = new BildeTilRedigeringsomraadeAdapter<>(this);
        return adapter.visSomRedigeringsomraade();
    }


    @Override
    public void instansOpprettFelter() {

        bildeOpplastingsKomponent = super.leggTilAndrefelterOver(
                new BildeOpplastingsKomponent(this.visSomRedigeringsomraade(), this)
            );


        tittelTextField = super.leggTilRedigeringsfelt(new TextField("Tittel"));
        beskrivelseTextArea = super.leggTilRedigeringsfelt(new TextArea("Beskrivelse"));
        erHovedbildeCheckbox = super.leggTilRedigeringsfelt(new Checkbox("Er hovedbilde"));
        erHovedbildeCheckbox.addClickListener(e -> oppdaterAndreHovedbildeCheckboxer());

        rekkefoelgeIntegerField = super.leggTilRedigeringsfelt(new IntegerField("Rekkefølge"));
        opprettFelter_leggTilBlurListener();

        filnavnTextField = super.leggTilRedigeringsfelt(new TextField("Filnavn"));
        stoerrelseIMbNumberField = super.leggTilRedigeringsfelt(new NumberField("Størrelse (Mb)"));
        hoeydeIPixlerIntegerField = super.leggTilRedigeringsfelt(new IntegerField("Høyde (px)"));
        breddeIPixlerIntegerField = super.leggTilRedigeringsfelt(new IntegerField("Bredde (px)"));

        super.setFokusComponent(tittelTextField);
    }

    private void oppdaterAndreHovedbildeCheckboxer() {
        Bildeklasse valgtBilde = getBildeentitet();
        ArrayList<Bildeklasse> bilder = hentOppdatertListeAvBilder();
        if (erHovedbildeCheckbox.getValue()==true && !valgtBilde.getErHovedbilde()) {
            // Nullstill de andre
            for (Bildeklasse bilde:bilder) {
                if (!valgtBilde.equals(getBildeentitet())) {
                    valgtBilde.setErHovedbilde(false);
                }
            }

        } else if (erHovedbildeCheckbox.getValue()==false && valgtBilde.getErHovedbilde()) {
            //Set første til hovedbilde
            int i = 0;
            for (Bildeklasse bilde:bilder) {
                if (i==0) {
                    bilde.setErHovedbilde(true);
                } else {
                    bilde.setErHovedbilde(false);
                }
                i++;
            }
        }

    }

    private void opprettFelter_leggTilBlurListener(){
        tittelTextField.addBlurListener(e -> lagreBildeinfo());
        beskrivelseTextArea.addBlurListener(e -> lagreBildeinfo());
        erHovedbildeCheckbox.addBlurListener(e ->lagreBildeinfo_oppdaterHovedbildeInfo());
        rekkefoelgeIntegerField.addBlurListener(e -> lagreBildeinfo_oppdaterRekkefoelge());
    }


    private void lagreBildeinfo_oppdaterRekkefoelge(){
        lagreBildeinfo();
        ArrayList<Bildeklasse> bilder = hentOppdatertListeAvBilder();
        Rekkefolgekyklop.hent().sorterOgoppdaterRekkefoelgenumre(bilder);
    }


    private ArrayList<Bildeklasse> hentOppdatertListeAvBilder() {
        if (getEntitet() instanceof EntitetMedForelderAktig) {
            EntitetMedForelderAktig bildeMedForelder =  (EntitetMedForelderAktig) getEntitet();
            EntitetMedBarnAktig<Bildeklasse> forelder = (EntitetMedBarnAktig<Bildeklasse>) bildeMedForelder.getForelder();
            //EntitetMedBarnAktig<Bildeklasse> forelder = (EntitetMedBarnAktig<Bildeklasse>) ((EntitetMedForelderAktig) getEntitet()).getForelder();
            return forelder.hentBarn();
        } else {
            Loggekyklop.hent().loggFEIL("Entiteten er ikke av type EntitetMedForelderAktig, returnerer tom liste");
            return new ArrayList<>();
        }


    }

    @Override
    public void lagringEtterOpplastingEllerValgAvFil(String nyttFilnavnString) {
        filnavnTextField.setValue(nyttFilnavnString);
        Bildekyklop.hent().oppdaterBildeentitetFraFilnavn(nyttFilnavnString, (AbstraktBildeentitet) getEntitet());
        lesBean();
        bildeOpplastingsKomponent.hentBildeFraFilnavn(filnavnTextField.getValue());
    }

    @Override
    public String getFilnavn() {
        return getEntitet().getFilnavn();
    }

    @Override
    public Filkategori getFilkategori() {
        return Filkategori.BILDE;
    }

    /**
     * Her settes alle andre erHovedBilde til false
     */
    private void lagreBildeinfo_oppdaterHovedbildeInfo(){
        lagreBildeinfo();
        ArrayList<Bildeklasse> bilder = hentOppdatertListeAvBilder();
        bilder.remove(getEntitet());
        for (BildeentitetAktig bilde:bilder) {
            bilde.setErHovedbilde(false);
        }
        bildeService.lagreAlle(bilder);
    }

    private void lagreBildeinfo(){
        skrivBean();
        bildeService.lagre(getEntitet());
    }

    @Override
    public void instansByggOppBinder() {
        super.hentBinder().bind(filnavnTextField, Bildeklasse::getFilnavn, Bildeklasse::setFilnavn);
        super.hentBinder().bind(tittelTextField, Bildeklasse::getTittel, Bildeklasse::setTittel);
        super.hentBinder().bind(stoerrelseIMbNumberField, Bildeklasse::getStoerrelseIMb, Bildeklasse::setStoerrelseIMb);
        super.hentBinder().bind(hoeydeIPixlerIntegerField, Bildeklasse::getHoeydeIPixler, Bildeklasse::setHoeydeIPixler);
        super.hentBinder().bind(breddeIPixlerIntegerField, Bildeklasse::getBreddeIPixler, Bildeklasse::setBreddeIPixler);
        super.hentBinder().bind(beskrivelseTextArea, Bildeklasse::getBeskrivelse, Bildeklasse::setBeskrivelse);
        super.hentBinder().bind(erHovedbildeCheckbox, Bildeklasse::getErHovedbilde, Bildeklasse::setErHovedbilde);
        super.hentBinder().bind(rekkefoelgeIntegerField, Bildeklasse::getRekkefoelge, Bildeklasse::setRekkefoelge);

    }


}
