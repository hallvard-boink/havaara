package com.hallvardlaerum.libs.feiloglogging;

import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Versjonskykloper sørger for å gjøre informasjon om siste oppdatert og tidligere versjon
 * tilgjengelig for bruker. Dette er en måte å formidle informasjon om nye funksjoner på,
 * og hjelper utvikler med å se hva som er gjort tidligere.
 */
public abstract class Versjonskyklopmal implements VersjonskyklopAktig {

    private String applikasjonsNavnString;
    private String applikasjonsKortnavnString;
    private String applikasjonsBeskrivelseString;
    private ArrayList<Versjon> versjonerArrayList;



    @Override
    public void leggTilVersjon(String versjonsNrString, String strDatoYYYY_MM_DD_String, String tittelString, String beskrivelseString) {
        if (versjonerArrayList == null) {
            versjonerArrayList = new ArrayList<>();
        }
        LocalDate datoLocalDate = Datokyklop.hent().opprettDatoSomYYYY_MM_DD(strDatoYYYY_MM_DD_String);
        versjonerArrayList.add(new Versjon(applikasjonsNavnString,versjonsNrString,datoLocalDate,tittelString,beskrivelseString));
    }

    @Override
    public String getApplikasjonsBeskrivelseString() {
        return applikasjonsBeskrivelseString;
    }

    @Override
    public void setApplikasjonsBeskrivelseString(String applikasjonsBeskrivelseString) {
        this.applikasjonsBeskrivelseString = applikasjonsBeskrivelseString;
    }

    @Override
    public ArrayList<Versjon> getVersjonerArrayList() {
        return versjonerArrayList;
    }


    @Override
    public String hentSisteVersjonSomStreng() {
        return versjonerArrayList.getFirst().lagKortUtgave();  //versjonene legges til med nyeste først. Konvensjon, altså.
    }

    @Override
    public Versjon hentSisteVersjon(){
        return versjonerArrayList.getFirst();
    }

    public abstract void byggOppVersjoner() ;

    @Override
    public String getApplikasjonsNavnString() {
        return applikasjonsNavnString;
    }

    @Override
    public void setApplikasjonsNavnString(String applikasjonsNavnString) {
        this.applikasjonsNavnString = applikasjonsNavnString;
    }

    @Override
    public String getApplikasjonsKortnavnString() {
        return applikasjonsKortnavnString;
    }

    public void setApplikasjonsKortnavnString(String applikasjonsKortnavnString) {
        this.applikasjonsKortnavnString = applikasjonsKortnavnString;
    }

}
