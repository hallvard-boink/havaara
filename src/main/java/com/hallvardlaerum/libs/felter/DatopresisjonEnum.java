package com.hallvardlaerum.libs.felter;

import com.hallvardlaerum.libs.database.EnumAktig;

public enum DatopresisjonEnum implements EnumAktig {

    FULL_DATO("Full dato","Hele datoen er kjent"),
    MAANED("Måned","Måned og år er kjent"),
    AAR("År","Kun årstallet er kjent"),
    UKJENT_DATO("Ukjent dato","Datoen er helt ukjent eller ikke bestemt ennå");

    private String tittel;
    private String beskrivelse;

    public static DatopresisjonEnum hentFraTittel(String tittel) {
        return EnumAktig.hentFraTittel(DatopresisjonEnum.class,tittel);
    }


//    public static DatopresisjonEnum hentFraTittel(String tittel) {
//        if (tittel==null || tittel.isEmpty()) {
//            return null;
//        }
//
//        for (DatopresisjonEnum d:DatopresisjonEnum.values()) {
//            if (d.getTittel().equalsIgnoreCase(tittel)) {
//                return d;
//            }
//        }
//        return null;
//    }

    DatopresisjonEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }

    @Override
    public String getTittel() {
        return tittel;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    @Override
    public String getBeskrivelse() {
        return beskrivelse;
    }

    public String getTittelIImportfil() {
        return tittel;
    }

    public String getTittelMedBeskrivelse() {
        return tittel + " (" + beskrivelse + ")";
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
