package com.hallvardlaerum.libs.feiloglogging;

import java.time.LocalDate;

public class Versjon {
    private String applikasjonsNavn;
    private String versjonsNr;
    private String tittel;
    private String beskrivelse;
    private LocalDate dato;


    /**
     * Legg til i kronologisk rekkefølge. Gidder ikke å sortere.
     * @param applikasjonsNavn
     * @param versjonsNr
     * @param dato
     * @param tittel
     * @param beskrivelse
     */
    public Versjon(String applikasjonsNavn, String versjonsNr, LocalDate dato, String tittel, String beskrivelse) {
        this.applikasjonsNavn=applikasjonsNavn;
        this.versjonsNr = versjonsNr;
        this.dato = dato;
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }

    public String lagKortUtgave() {
        StringBuilder sb = new StringBuilder();
        sb.append(applikasjonsNavn).append(" v");
        sb.append(versjonsNr).append(" ");
        sb.append(dato).append(" ");
        sb.append(tittel);
        return sb.toString();
    }

    public String lagUtgaveMedBeskrivelse(){
        return lagKortUtgave() + ": " + getBeskrivelse();
    }

    public String getVersjonsNr() {
        return versjonsNr;
    }

    public void setVersjonsNr(String versjonsNr) {
        this.versjonsNr = versjonsNr;
    }

    public String getTittel() {
        return tittel;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public LocalDate getDato() {
        return dato;
    }

    public void setDato(LocalDate dato) {
        this.dato = dato;
    }

    public String getApplikasjonsNavn() {
        return applikasjonsNavn;
    }

    public void setApplikasjonsNavn(String applikasjonsNavn) {
        this.applikasjonsNavn = applikasjonsNavn;
    }
}