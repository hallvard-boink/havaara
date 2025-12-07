package com.hallvardlaerum.libs.eksportimport;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.database.EntitetserviceMedForelderAktig;
import com.hallvardlaerum.libs.database.EnumAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.felter.TekstKyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <h1>CSVImportkyklop</h1>
 *
 * @author Hallvard Lærum
 * <p>
 * Henter tekstrader fra Upload-komponenten i Vaadin
 * Forutsetter at csv-filen har header, dvs. at første rad i tekstfilen viser feltnavnene
 * Er beregnet på å importere csv-filer som er eksportert med CSVEksportkyklop
 * Forutsetter at entiteten er kjent, ved at spesifikk entitetservice oppgis
 * Forutsetter at entiteten ekstenderer AkstraktEntitet.</p>
 * <br/>
 * <b>Singleton-mønsteret er forlatt</b>. <br/>
 * Bruk CSVImportMester i stedet, som er et vanlig objekt, med constructor.
 */


public class CSVImportmester implements CSVImportmesterAktig {
    private final Integer maksAntallEntiteterIApplikasjonen = 100;
    private ArrayList<String> arraylistFeltnavnTilImport;
    private String delimiter = ";";
    private int antallFelteriEntiteten;
    private EntitetserviceAktig entitetservice;
    private boolean blnBareKjoerPaa = true;
    private String importradString;
    private CSVImportassistentAktig csvImportassistentAktig = null;
    private String lesCharsetString="UTF-8";



    //Dialogboks lastOppFil
    private Dialog importdialog;
    private VerticalLayout verticalLayoutImportdialog;
    private TextField textFieldFilnavn;
    private TextField textFieldAntallrader;
    private ArrayList<String> alInnholdImport;
    private Button buttonKjoerImport;
    private Button buttonAvbrytImport;


    public CSVImportmester(CSVImportassistentAktig csvImportassistentAktig) {
        this.csvImportassistentAktig = csvImportassistentAktig;
        opprettDialogboks();
    }

    public CSVImportmester() {
        opprettDialogboks();
    }

    @Override
    public CSVImportassistentAktig getCsvImportassistentAktig() {
        return csvImportassistentAktig;
    }

    @Override
    public void setCsvImportassistentAktig(CSVImportassistentAktig csvImportassistentAktig) {
        this.csvImportassistentAktig = csvImportassistentAktig;
    }

    @Override
    public String getLesCharsetString() {
        return lesCharsetString;
    }

    @Override
    public void setLesCharsetString(String lesCharsetString) {
        this.lesCharsetString = lesCharsetString;
    }

    private void opprettDialogboks() {
        importdialog = new Dialog();
        verticalLayoutImportdialog = new VerticalLayout();

        textFieldFilnavn = new TextField("Opplastet fil");
        textFieldAntallrader = new TextField("Antall rader");

        HorizontalLayout horizontalLayoutTekstfelter = new HorizontalLayout();
        horizontalLayoutTekstfelter.setWidthFull();
        horizontalLayoutTekstfelter.add(textFieldFilnavn, textFieldAntallrader);

        InMemoryUploadHandler buffer = UploadHandler.inMemory((metadata, data) -> {
            String strFileName = metadata.fileName();
            String mimeType = metadata.contentType();
            long contentLength = metadata.contentLength();


            Charset charset = Charset.forName(lesCharsetString);
            String strLines[] = new String(data, charset).split("\\r?\\n");

            alInnholdImport = new ArrayList<>(Arrays.asList(strLines));

            textFieldFilnavn.setValue(strFileName);
            textFieldAntallrader.setValue(Integer.toString(alInnholdImport.size()));
            buttonKjoerImport.setEnabled(true);
        });
        Upload upload = new Upload(buffer);


        buttonKjoerImport = new Button("Kjør import");
        buttonKjoerImport.setEnabled(false);
        buttonKjoerImport.addClickListener(e -> {
            if (csvImportassistentAktig!=null) {
                csvImportassistentAktig.forberedImport();
            }
            importerFraArrayListMedStrenger(alInnholdImport);
            if (csvImportassistentAktig!=null) {
                csvImportassistentAktig.ryddOppEtterImport();
            }

            importdialog.close();
            opprettDialogboks();
        });


        buttonKjoerImport.setEnabled(false);
        buttonKjoerImport.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonAvbrytImport = new Button("Avbryt import");
        buttonAvbrytImport.addClickListener(e -> {
            importdialog.close();
            opprettDialogboks();
        });

        HorizontalLayout horizontalLayoutButtons = new HorizontalLayout();
        horizontalLayoutButtons.setWidthFull();
        horizontalLayoutButtons.add(buttonKjoerImport, buttonAvbrytImport);

        verticalLayoutImportdialog.add(upload, horizontalLayoutTekstfelter, horizontalLayoutButtons);
        importdialog.add(verticalLayoutImportdialog);
        verticalLayoutImportdialog.setSizeFull();

    }


    /**
     * Dette er hovedmetoden som starter det hele
     *
     * @param entitetserviceAktig
     */
    //@Override
    public void velgImportfilOgKjoerImport(EntitetserviceAktig<?,?> entitetserviceAktig) {
        this.entitetservice = entitetserviceAktig;
        importdialog.open();
    }




    /**
     * Denne brukes av dialogboksen til selve importen
     *
     * @param arrayListCSVInnhold
     */
    @Override
    public boolean importerFraArrayListMedStrenger(ArrayList<String> arrayListCSVInnhold) {
        //TODO: Lag mulighet for merge (velg unik id, bruk den til søk, ikke erstatt eksisterende verdier med null eller "")

        if (arrayListCSVInnhold == null || arrayListCSVInnhold.isEmpty()) {
            Loggekyklop.hent().loggFEIL("ArrayList var null eller tom, avbryter.");
            return false;
        }

        Loggekyklop.hent().initierLoggfil();
        Loggekyklop.hent().huskStatus();
        Loggekyklop.hent().settNivaaINFO();
        Loggekyklop.hent().ikkeVisNotifikasjonerTilBruker();

        oppdater_delimiter(arrayListCSVInnhold.getFirst()); // Bruk første rad i tekstfilen til å velge delimiter, så slipper brukeren å gjøre det

        if (csvImportassistentAktig == null) {
            standardimporteringavtekst(arrayListCSVInnhold);
        } else {
            importerMedCSVKonverteringsAktig(arrayListCSVInnhold);
        }
        return true;
    }

    private void importerMedCSVKonverteringsAktig(ArrayList<String> arrayListCSVInnhold) {
        String[] feltnavnene = arrayListCSVInnhold.getFirst().split(delimiter);
        ArrayList<String> feltnavneneArrayList = new ArrayList<>(List.of(feltnavnene));
        for (int i = 1; i<arrayListCSVInnhold.size(); i++) {
            String[] celler = arrayListCSVInnhold.get(i).split(delimiter);
            csvImportassistentAktig.konverterFraTekstRadOgLagre(feltnavneneArrayList,celler);
        }

    }

    private void standardimporteringavtekst(ArrayList<String> arrayListCSVInnhold){
        oppdaterArrayListFeltnavnTilImport(arrayListCSVInnhold.getFirst()); // Bygg opp liste over felter som skal importeres

        Loggekyklop.hent().loggTilFilINFO("Antall rader i importfil: " + arrayListCSVInnhold.size());
        Loggekyklop.hent().loggTilFilINFO("Delimiter er '" + delimiter + "'");

        ArrayList<EntitetAktig> arrayListEntiteter = new ArrayList<>();
        for (int i = 1; i < arrayListCSVInnhold.size(); i++) {
            arrayListEntiteter.add((EntitetAktig) opprettOgFyllEntitet(arrayListCSVInnhold.get(i))); // Selve importen, strengrad for strengrad
        }

        entitetservice.flush();
        entitetservice.lagreAlle(arrayListEntiteter);
        entitetservice.flush();

        Loggekyklop.hent().lukkLoggfil();
        Loggekyklop.hent().tilbakestillStatus();


        return ;
    }

    public boolean importerFraFileTilKjentEntitet(Path importFilPath, EntitetserviceAktig<?,?> entitetservice) {
        this.entitetservice = entitetservice;
        try {
            ArrayList<String> tekstraderArrayList = new ArrayList<>(Files.readAllLines(importFilPath));
            return importerFraArrayListMedStrenger(tekstraderArrayList);

        } catch (IOException e) {
            Loggekyklop.hent().loggFEIL("Klarte ikke å lese importfilen " + importFilPath.toFile() + ", avbryter");
            return false;
        }

    }


    /**
     * Når Spring Boot eksporterer beans, blir eventuelle fremmednøkler presentert sammen med navnet på klassen.
     * Denne må fjernes før UUID kan brukes til import.
     * Det er hensikten med denne prosedyren.
     * <p>
     * Eksempel: "Forfatter{id=049e5bc0-65d4-4d31-b038-0607ceff9297}"
     *
     * @param raatekst tekst med klassenavn, klammeparentes, 'id=' og uuid
     * @return bare uuid
     */
    public String hentUt_UUID(String raatekst) {
        // Forfatter{id=049e5bc0-65d4-4d31-b038-0607ceff9297}
        return hentUt_EntitetsnavnOgUuid(raatekst)[1];
    }


    /**
     * Denne kan brukes til å finne ut hvilken javabean fremmednøkkelen peker på
     *
     * @param raatekst
     * @return klassenavn
     */
    public String hentUt_Entitetsnavn(String raatekst) {
        return hentUt_EntitetsnavnOgUuid(raatekst)[0];
    }


    /**
     * Når Spring Boot eksporterer beans, blir eventuelle fremmednøkler presentert sammen med navnet på klassen.
     * Denne prosedyren henter ut både klassenavn og uuid, og returnerer de som en String[]
     * <p>
     * Eksempel: "Forfatter{id=049e5bc0-65d4-4d31-b038-0607ceff9297}"
     *
     * @param raatekst tekst med klassenavn, klammeparentes, 'id=' og uuid
     * @return String[] hvor String[0] er klassenavn og String[1] er uuid
     */
    public String[] hentUt_EntitetsnavnOgUuid(String raatekst) {
        String[] arKomponenter = new String[2];
        if (raatekst.contains("{id=")) {
            String[] deler = raatekst.split("\\{id\\=");
            if (deler.length == 2) {
                arKomponenter[0] = deler[0];
                arKomponenter[1] = deler[1].substring(0, deler[1].length() - 1);
                return arKomponenter;
            } else {
                Loggekyklop.hent().loggDEBUG("Noe er feil med splitt");
                return arKomponenter;
            }
        } else {
            Loggekyklop.hent().loggDEBUG("Fant ikke id i teksten");
            return arKomponenter;
        }

    }

    /**
     * Brukes til å hente ut klassenavn direkte fra csv-filen, slik at det blir mulig å opprett entiteter
     * med relasjonskoblinger.
     *
     * @param strVerdi
     * @return navnet på klassen
     */
    @Override
    public String hentUtKlassenavn(String strVerdi){
        String strFoerstedel = TekstKyklop.hent().hentFoersteDelAvStrengMedDelimiter(strVerdi,"@");
        return TekstKyklop.hent().hentSisteIStrengMedDelimiter(strFoerstedel,".");
    }

    /**
     * Her skjer selve importen.
     *
     * @param strRad
     */
    private Object opprettOgFyllEntitet(String strRad) {
        importradString = strRad;
        EntitetAktig entitet = entitetservice.opprettEntitet();
        String[] verdiene = strRad.split(delimiter);
        for (int i = 0; i < verdiene.length; i++) {
            if (i<arraylistFeltnavnTilImport.size()) {  //Noen ganger eksport av regnark med flere tomme kolonner
                if (!arraylistFeltnavnTilImport.get(i).isEmpty()) {
                    importerEntitet_oppdaterFelt(entitet, arraylistFeltnavnTilImport.get(i), verdiene[i]);
                }
            }
        }
        return entitet;

    }

    /**
     * Her blir hvert felt fyllt med data
     *
     * @param entitet
     * @param feltnavn
     * @param nyVerdi
     */
    public void importerEntitet_oppdaterFelt(EntitetAktig entitet, String feltnavn, Object nyVerdi) {
        if (nyVerdi == null || ((String) nyVerdi).isEmpty()) {
            return;
        }

        Field field = hentField(entitet, feltnavn);  //Få tak i feltet i klassen eller superklassen
        if (field == null) {
            entitetservice.lagreEkstrafeltTilSenere(entitet, feltnavn, (String)nyVerdi, importradString);
            return;
        }

        if (field.getName().equalsIgnoreCase("uuid")) {
            importerEntitet_oppdaterFelt_UUID(entitet, field, nyVerdi);
            return;
        }

        if (entitetservice.behandleSpesialfeltVedImport(entitet, field, (String) nyVerdi, importradString)) {
            // Skal returnere false hvis feltet ikke er behandlet
            return;
        }


        if (field.isAnnotationPresent(ManyToOne.class)) {
            if (entitetservice instanceof EntitetserviceMedForelderAktig<?, ?, ?, ?>) {
                ((EntitetserviceMedForelderAktig<?, ?, ?, ?>) entitetservice).oppdaterForelderVedImport(entitet, field, (String) nyVerdi);
            } else {
                Loggekyklop.hent().loggTilFilINFO("Feltet " + field.getName() + " er annotert med @ManyToOne, men entitetservicen implementerer ikke EntitetserviceMedForelderAktig<?,?>. "
                + "Importerer ikke feltets innhold.");
            }
            return;
        }


        if (field.getType() == Integer.class || field.getType() == int.class) {
            importerEntitet_oppdaterFelt_Integer(entitet, field, nyVerdi);

        } else if (field.getType() == Float.class || field.getType() == float.class) {
            importerEntitet_oppdaterFelt_Float(entitet, field, nyVerdi);

        } else if (field.getType() == Double.class || field.getType() == double.class) {
            importerEntitet_oppdaterFelt_Double(entitet, field, nyVerdi);

        } else if (field.getType() == String.class) {
            importerEntitet_oppdaterFelt_String(entitet, field, nyVerdi);

        } else if (field.getType() == LocalDate.class) {
            importerEntitet_oppdaterFelt_LocalDate(entitet, field, nyVerdi);

        } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
            importerEntitet_oppdaterFelt_Boolean(entitet, field, nyVerdi);

        } else if (EnumAktig.class.isAssignableFrom(field.getType())) {
            importerEntitet_oppdaterFelt_EnumAktig(entitet, field, nyVerdi);

        } else {
            importerEntitet_oppdaterFelt_loggFeil(entitet,null,nyVerdi);
        }

    }

    private void importerEntitet_oppdaterFelt_Double(EntitetAktig entitet, Field field, Object nyVerdi) {
        try {
            String strNyVerdi = (String)nyVerdi;
            strNyVerdi = strNyVerdi.replace(",", ".");
            field.set(entitet, Double.parseDouble(strNyVerdi));

        } catch (IllegalAccessException e) {
            importerEntitet_oppdaterFelt_loggFeil(entitet, field, nyVerdi);
        }
    }

    private void importerEntitet_oppdaterFelt_Float(EntitetAktig entitet, Field field, Object nyVerdi) {
        try {
            String strNyVerdi = (String)nyVerdi;
            strNyVerdi = strNyVerdi.replace(",", ".");
            field.set(entitet, Float.parseFloat(strNyVerdi));

        } catch (IllegalAccessException e) {
            importerEntitet_oppdaterFelt_loggFeil(entitet, field, nyVerdi);
        }
    }

    private void importerEntitet_oppdaterFelt_EnumAktig(EntitetAktig entitet, Field field, Object nyVerdi) {
        Class<? extends Enum> enumClass = field.getType().asSubclass(Enum.class);

        Enum[] enumConstants = enumClass.getEnumConstants();

        // Search through all enum values
        for (Enum<?> enumConstant : enumConstants) {
            EnumAktig enumValue = (EnumAktig) enumConstant;
            if (nyVerdi.equals(enumValue.getTittel()) || nyVerdi.equals(enumValue.getTittelIImportfil())) {
                try {
                    field.set(entitet, enumConstant);
                } catch (IllegalAccessException e) {
                    importerEntitet_oppdaterFelt_loggFeil(entitet, field, nyVerdi);
                    return;
                }
                return;
            }
        }

        importerEntitet_oppdaterFelt_loggFeil(entitet, field, nyVerdi);

    }

    private void importerEntitet_oppdaterFelt_UUID(EntitetAktig entitet, Field field, Object nyVerdi) {
        if (nyVerdi==null) {
            return;
        }
        try {
            field.set(entitet, UUID.fromString((String) nyVerdi));
        } catch (IllegalAccessException e) {
            importerEntitet_oppdaterFelt_loggFeil(entitet, field, nyVerdi);
        }
    }

    private void importerEntitet_oppdaterFelt_Boolean(EntitetAktig entitet, Field field, Object nyVerdi) {
        if (nyVerdi==null) {
            return;
        }
        String verdiString = (String)nyVerdi;
        boolean verdiBoolean;
        if (verdiString.equalsIgnoreCase("yes") || verdiString.equalsIgnoreCase("y") || verdiString.equalsIgnoreCase("1")) {
            verdiBoolean = true;
        } else if (verdiString.equalsIgnoreCase("no") || verdiString.equalsIgnoreCase("n") ||verdiString.equalsIgnoreCase("0")) {
            verdiBoolean = false;
        } else {
            verdiBoolean = Boolean.parseBoolean(verdiString);
        }

        try {
            field.set(entitet, verdiBoolean);
        } catch (IllegalAccessException e) {
            importerEntitet_oppdaterFelt_loggFeil(entitet, field, nyVerdi);
        }
    }


    private void importerEntitet_oppdaterFelt_LocalDate(EntitetAktig entitet, Field field, Object nyVerdi) {
        String nyVerdiString = "";

        if (nyVerdi==null) {
            return;
        } else {
            nyVerdiString = (String)nyVerdi;
        }

        LocalDate datoLocalDate = Datokyklop.hent().opprettDatoSomYYYY_MM_DD(nyVerdiString);
        if (datoLocalDate==null) {
            datoLocalDate = Datokyklop.hent().opprettDatoSom_DDpMMpYYYY(nyVerdiString);
            if (datoLocalDate == null) {
                try {
                    datoLocalDate = LocalDate.parse(nyVerdiString);
                    if (datoLocalDate == null) {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }

            }
        }

        try {
            field.set(entitet, datoLocalDate);
        } catch (IllegalAccessException e) {
            importerEntitet_oppdaterFelt_loggFeil(entitet, field, nyVerdi);
        }
    }

    private void importerEntitet_oppdaterFelt_String(EntitetAktig entitet, Field field, Object nyVerdi) {
        String tekst = (String) nyVerdi;
        if (field.isAnnotationPresent(Size.class)) {
            Size size = field.getAnnotation(Size.class);
            if (tekst.length() > size.max()) {
                nyVerdi = tekst.substring(0, size.max());
            }
        }
        try {
            field.set(entitet, nyVerdi);
        } catch (IllegalAccessException e) {
            importerEntitet_oppdaterFelt_loggFeil(entitet, field, nyVerdi);
        }
    }

    private void importerEntitet_oppdaterFelt_Integer(EntitetAktig entitet, Field field, Object nyVerdi) {
        if (nyVerdi==null) {
            return;
        }
        String nyVerdiString = String.valueOf(nyVerdi).trim();
        field.setAccessible(true);

        Integer nyVerdiInteger = HelTallMester.konverterStrengMedDesimalTilInteger(nyVerdiString);
        if (nyVerdiInteger==null) {

            return;
        }

        try {
            field.set(entitet, nyVerdiInteger);
        } catch (Exception e) {
            importerEntitet_oppdaterFelt_loggFeil(entitet, field, nyVerdi);
        }
    }

    private void importerEntitet_oppdaterFelt_loggFeil(EntitetAktig entitet, Field field, Object nyVerdi){
        String feltnavnString = field != null? field.getName():"(ukjent felt)";

        String meldingString = "Kunne ikke sette " + feltnavnString + " til verdien " + nyVerdi
                + " for entiteten " + entitet.hentBeskrivendeNavn() + "(" + entitet.getClass().getName() + ") Importrad: " + importradString;
        Loggekyklop.hent().loggTilFilINFO(meldingString);

//        Loggekyklop.hent().loggFEIL("Kunne ikke sette " + field.getName() + " til verdien " + nyVerdi
//                + " for entiteten " + entitet.hentBeskrivendeNavn() + "(" + entitet.getClass().getName() + ")");
    }

    private Field hentField(Object entitet, String strFeltnavn) {
        Field field = null;
        try {
            field = entitet.getClass().getDeclaredField(strFeltnavn);
        } catch (Exception e) {
            try {
                field = entitet.getClass().getSuperclass().getDeclaredField(strFeltnavn);
            } catch (NoSuchFieldException noSuchFieldException) {
                //Loggekyklop.hent().loggFEIL("CSVImportkyklop: Feltet \"" + strFeltnavn + "\" finnes ikke i entiteten " + entitet.getClass().getName());
                return null;
            }
        }

        try {
            field.setAccessible(true); // Allow access to private fields
        } catch (SecurityException e) {
            Loggekyklop.hent().loggFEIL("CSVImportkyklop: Kunne ikke gi tilgang til feltet " + strFeltnavn +
                    " i entiteten " + entitet.getClass().getName() + ". Fortsetter import av resterende felter.");
            return null;
        }

        return field;
    }



    public void oppdaterArrayListFeltnavnTilImport(String feltnavnradString) {
        if (feltnavnradString.isBlank()) {
            Loggekyklop.hent().loggFEIL("strFeltnavnrad er tom, avbryter.");
            return;
        }

        if (arraylistFeltnavnTilImport==null) {
            arraylistFeltnavnTilImport = new ArrayList<>();
        }

        // Splitte
        String[] feltnavnene = feltnavnradString.split(delimiter);
        if (feltnavnene.length==0) {
            Loggekyklop.hent().loggTilFilADVARSEL("Ingen felter funnet, avbyter");
            return;
        }

        // Bygge opp arraylist av alle feltene i entiteten som det skal importeres til
        EntitetAktig entitet = entitetservice.opprettEntitet();
        ArrayList<Field> felteneiEntitetenArrayList = new ArrayList<>(List.of(entitet.getClass().getDeclaredFields()));
        Field[] forelderFelter = entitet.getClass().getSuperclass().getDeclaredFields();
        felteneiEntitetenArrayList.addAll(Arrays.asList(forelderFelter));

        // Finne de faktiske feltene og legge dem til i arraylistFeltnavnTilImport - uavhengig av små eller store bokstaver!
        for (int i = 0; i<feltnavnene.length; i++) {
            boolean funnetBoolean = false;
            for (Field field:felteneiEntitetenArrayList) {
                if (field.getName().equalsIgnoreCase(feltnavnene[i])) {
                    arraylistFeltnavnTilImport.add(field.getName());
                    funnetBoolean = true;
                    break;
                }
            }
            if (!funnetBoolean) {
                Loggekyklop.hent().loggTilFilINFO("Fant ikke feltet med navnet " + feltnavnene[i] + " i entiteten " + entitet.getClass().getSimpleName());
                arraylistFeltnavnTilImport.add(feltnavnene[i]);
            }
        }

        // Printe ut navnene i loggfilen
        StringBuilder sb = new StringBuilder();
        sb.append("Fant følgende ").append(arraylistFeltnavnTilImport.size()).append(" felter: ");
        for (String feltnavn : arraylistFeltnavnTilImport) {
            sb.append(feltnavn).append(" ");
        }
        Loggekyklop.hent().loggTilFilINFO(sb.toString());

    }


    @Override
    public String getDelimiter() {
        return delimiter;
    }


    public boolean oppdater_delimiter(String feltnavnrad) {
        String[] delimiters = {",", ";", "\\|", "\t"};
        antallFelteriEntiteten = 200; //Et estimat

        int intDel = 0;
        for (String del : delimiters) {
            String[] feltnavnene = feltnavnrad.split(delimiters[intDel]);
            if (feltnavnene.length >= 2 && feltnavnene.length <= antallFelteriEntiteten) {
                delimiter = delimiters[intDel];
                return true;
            }
            intDel++;
        }

        Loggekyklop.hent().loggFEIL("CSVImportkyklop.oppdater_delimiter: Fant ikke standard delimiter, setter Go=false");
        blnBareKjoerPaa = false;
        return false;
    }




}
