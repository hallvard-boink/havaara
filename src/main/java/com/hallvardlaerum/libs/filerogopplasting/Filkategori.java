package com.hallvardlaerum.libs.filerogopplasting;

public enum Filkategori {
    BILDE("bilder", "bilde"),
    BACKUP("backups","backup"),
    PDF("PDFer","PDF"),
    ANNET("andrefiler", "fil");

    Filkategori(String mappeNavn, String visningsNavn){
        this.mappeNavn = mappeNavn;
        this.visningsNavn = visningsNavn;
    }

    private String mappeNavn;

    private String visningsNavn;

    public String getVisningsNavn() {
        return visningsNavn;
    }

    public void setVisningsNavn(String visningsNavn) {
        this.visningsNavn = visningsNavn;
    }

    public String getMappeNavn() {
        return mappeNavn;
    }

    public void setMappeNavn(String mappeNavn) {
        this.mappeNavn = mappeNavn;
    }
}