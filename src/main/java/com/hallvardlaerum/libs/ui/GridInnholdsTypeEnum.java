package com.hallvardlaerum.libs.ui;

public enum GridInnholdsTypeEnum {
    ALLERADER("Alle rader samtidig","Alle rader leses inn i grid samtidig. Denne typen egner seg kun til små tabeller, men er enklere å sette opp."),
    PORSJONSVIS("Porsjonsvis","Nok rader leses inn til å fylle grid som er på skjermen. Denne typen egner seg også til store tabeller.")
    ;

    private String tittel;
    private String beskrivelse;

    GridInnholdsTypeEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }
}
