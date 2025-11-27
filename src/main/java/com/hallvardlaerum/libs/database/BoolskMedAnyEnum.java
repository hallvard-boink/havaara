package com.hallvardlaerum.libs.database;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Denne kan brukes når combobokser skal brukes som filterfelt for boolske felter.
 * Den inkluderer true, false og any
 */
public enum BoolskMedAnyEnum {

    TRUE("sann","Verdien er sann evt. true"),
    FALSE("usann","Verdien er usann evt. false"),
    ANY("alle","Alle verdier av dette feltet");

    private String tittel;
    private String beskrivelse;

    BoolskMedAnyEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
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

    public static ArrayList<BoolskMedAnyEnum> hentEnumSomArrayList(){
        ArrayList<BoolskMedAnyEnum> boolskMedAnyEnums = new ArrayList<>();
        Collections.addAll(boolskMedAnyEnums, BoolskMedAnyEnum.values());
        return boolskMedAnyEnums;
    }
}
