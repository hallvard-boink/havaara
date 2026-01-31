package com.hallvardlaerum.libs.eksportimport;


import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.feiloglogging.LoggekyklopAktig;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.TekstKyklop;
import com.vaadin.flow.component.notification.Notification;
import jakarta.persistence.Entity;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * Forutsetter at entiteten har ekstendert AkstraktEntitet
 */
public class CSVEksportkyklop implements CSVEksportkyklopAktig {
    private static CSVEksportkyklop csvEksportkyklop;
    private String delimiter = ";";
    private ArrayList<Field> feltListe;
    private Boolean blnLeggTilTidspunktIFilnavn = true;
    private String strFilnavn;



    public static CSVEksportkyklop hent(){
        if (csvEksportkyklop == null) {
            csvEksportkyklop = new CSVEksportkyklop();
        }
        return csvEksportkyklop;
    }

    private CSVEksportkyklop() {
    }


    public String getDelimiter() {
        return delimiter;
    }

    @Override
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }



    // --------
    // Denne brukes!
    // --------
    @Override

    public void eksporterArrayListTilTekst(String entitetsnavnString, ArrayList objekterArrayList) {
        byggOppFeltliste(objekterArrayList.get(0));
        if (feltListe.isEmpty()) {
            Loggekyklop.bruk().loggFEIL("Ingen felter i bean er merket med @SkalEksporteres. Ingen felter blir dermed eksportert, avbryter.");
            return;
        }

        if (blnLeggTilTidspunktIFilnavn) {
            strFilnavn = entitetsnavnString + "_" + Datokyklop.hent().hentNaavaerendeTidspunktSomDatoTid();
        }
        strFilnavn = strFilnavn + ".csv";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(strFilnavn)));
            writer.write(lagCSVTekstMedHeaderAvArrayList(objekterArrayList));
            writer.close();
            Notification.show("Eksporterte teksten til filen " + strFilnavn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String hentUtEntiteteneSomTekst(EntitetserviceAktig<? extends EntitetAktig, ?> entitetservice){
        return lagCSVTekstMedHeaderAvArrayList(entitetservice.finnAlle());
    }

    private String lagCSVTekstMedHeaderAvArrayList(ArrayList arrayList) {
        StringBuilder sb = new StringBuilder();

        Object o = arrayList.get(0);
        sb.append(hentFeltnavn_UtFraFeltliste(o));
        for (Object r:arrayList) {
            sb.append("\n");
            sb.append(hentVerdier_UtFraFeltListe(r));
        }
        return sb.toString();
    }



    private String hentVerdier_UtFraFeltListe(Object objectParameter) {
        EntitetAktig entitet;
        if (!(objectParameter instanceof EntitetAktig)) {
            Loggekyklop.bruk().loggADVARSEL("Objektet " + objectParameter.toString() + " er ikke en entitet. Dette skal normalt ikke gå an. Sjekk Havaara!");
            return "";
        } else {
            entitet = (EntitetAktig) objectParameter;
        }

        if (feltListe.isEmpty()) {
            Loggekyklop.bruk().loggADVARSEL("Du har glemt å oppdatere feltlista. Ikkeno problem, jeg har fiksa for deg. Men det er slurv, altså!");
            byggOppFeltliste(entitet);
        }

        StringBuilder sbFields = new StringBuilder();
        Integer intTeller = 0;
        for (Field field:feltListe) {
            intTeller++;
            if (intTeller>1) {
                sbFields.append(delimiter);
            }


            String valueString = "";
            try {
                Object value = field.get(entitet);
                if (value!=null) {
                    if (value instanceof EntitetAktig) {
                        valueString = ((EntitetAktig) value).getUuid().toString();
                    } else {
                        valueString = value.toString();
                    }
                } else {
                    valueString = "";
                }
            } catch (Exception e) {
                Loggekyklop.bruk().loggADVARSEL("Klarte ikke å hente ut verdien av feltet " + field.getName() + " av objektet " + objectParameter);
            }

            valueString = TekstKyklop.hent().fjernNylinjeFraTekstfelt(valueString);
            sbFields.append(valueString);
        }
        return sbFields.toString();
    }



    private String hentFeltnavn_UtFraFeltliste(Object o) {
        if (feltListe==null || feltListe.isEmpty()) {
            byggOppFeltliste(o);
        }

        StringBuilder sbFields = new StringBuilder();
        Integer intTeller = 0;
        for (Field f:feltListe) {
            intTeller++;
            if (intTeller>1) {
                sbFields.append(delimiter);
            }
            sbFields.append(f.getName());
        }
        return sbFields.toString();
    }

    public void oppdaterFeltliste(EntitetserviceAktig<?,?> entitetservice) {
        Object o = entitetservice.opprettEntitet();
        byggOppFeltliste(o);
    }

    private void byggOppFeltliste(Object o){
        feltListe = new ArrayList<>();
        Field[] arFields = o.getClass().getDeclaredFields();
        for (Field f:arFields) {
            if (f.isAnnotationPresent(SkalEksporteres.class)) {
                f.setAccessible(true);
                feltListe.add(f);
            }
        }
        Field[] arFieldsSuper = o.getClass().getSuperclass().getDeclaredFields();
        for (Field f:arFieldsSuper) {
            if (f.isAnnotationPresent(SkalEksporteres.class)) {
                f.setAccessible(true);
                feltListe.add(f);
            }
        }
    }

    private void leggTilTidspunktIFilnavn(Boolean verdi){
        this.blnLeggTilTidspunktIFilnavn = verdi;
    }



}
