package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.AbstraktEntitet;
import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstraktBildeentitet<Forelderklasse extends EntitetAktig> extends AbstraktEntitet
        implements BildeentitetAktig<Forelderklasse>{

    @SkalEksporteres
    private String filnavn;

    @SkalEksporteres
    private String tittel;

    @SkalEksporteres
    private Double stoerrelseIMb;

    @SkalEksporteres
    private Integer hoeydeIPixler;

    @SkalEksporteres
    private Integer breddeIPixler;

    @SkalEksporteres
    private String beskrivelse;

    @SkalEksporteres
    private Boolean erHovedbilde;

    @SkalEksporteres
    private Integer rekkefoelge;



    public Integer getRekkefoelge() {
        return rekkefoelge;
    }

    public void setRekkefoelge(Integer rekkefoelge) {
        this.rekkefoelge = rekkefoelge;
    }

    public Boolean getErHovedbilde() {
        return erHovedbilde;
    }

    public void setErHovedbilde(Boolean erHovedbilde) {
        this.erHovedbilde = erHovedbilde;
    }

    public String getFilnavn() {
        return filnavn;
    }

    public void setFilnavn(String filnavn) {
        this.filnavn = filnavn;
    }

    public String getTittel() {
        return tittel;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public Double getStoerrelseIMb() {
        return stoerrelseIMb;
    }

    public void setStoerrelseIMb(Double stoerrelseIMb) {
        this.stoerrelseIMb = stoerrelseIMb;
    }

    public Integer getHoeydeIPixler() {
        return hoeydeIPixler;
    }

    public void setHoeydeIPixler(Integer hoeydeIPixler) {
        this.hoeydeIPixler = hoeydeIPixler;
    }

    public Integer getBreddeIPixler() {
        return breddeIPixler;
    }

    public void setBreddeIPixler(Integer breddeIPixler) {
        this.breddeIPixler = breddeIPixler;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
