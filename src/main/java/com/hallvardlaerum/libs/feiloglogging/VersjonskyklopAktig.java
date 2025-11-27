package com.hallvardlaerum.libs.feiloglogging;

import java.util.ArrayList;

public interface VersjonskyklopAktig {


    ArrayList<Versjon> getVersjonerArrayList();
    String hentSisteVersjonSomStreng();
    Versjon hentSisteVersjon();

    void byggOppVersjoner();
    void leggTilVersjon(String versjonsNr, String strDatoYYYY_MM_DD, String tittel, String beskrivelse);

    void setApplikasjonsNavnString(String applikasjonsNavnString);
    String getApplikasjonsNavnString();

    void setApplikasjonsKortnavnString(String applikasjonsKortnavnString);
    String getApplikasjonsKortnavnString();

    void setApplikasjonsBeskrivelseString(String beskrivelse);
    String getApplikasjonsBeskrivelseString();
}
