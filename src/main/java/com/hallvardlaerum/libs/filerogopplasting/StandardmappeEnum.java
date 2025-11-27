package com.hallvardlaerum.libs.filerogopplasting;

public enum StandardmappeEnum {

    BACKUPS("backups","Her legges komprimerte filer med backups av alle dataene i applikasjonen, i klartekst."),
    UNZIP("unzip","Midlertidig mappe for å pakke ut innholdet av en backupfil. Brukes ved restore."),
    BILDER ("bilder","Her legges opplastede bilder og fotografier"),
    TEMP("temp","Her legges ymse midlertidige filer som skal slettes når de ikke trengs lenger."),
    DOCS("docs","Her legges opplastede dokumenter; pdf, wordfiler, m.m."),
    LOGS("logs","Her legges importlogger og andre logger, hvis aktivert.");


    public static StandardmappeEnum fraMappenavn(String mappenavnParameter) {
        for (StandardmappeEnum standardmappeEnum:StandardmappeEnum.values()) {
            if (standardmappeEnum.getMappenavn().equals(mappenavnParameter)) {
                return standardmappeEnum;
            }
        }
        return null;
    }

    private String mappenavn;
    private String beskrivelse;

    StandardmappeEnum(String mappenavn, String beskrivelse) {
        this.mappenavn = mappenavn;
        this.beskrivelse = beskrivelse;
    }

    public String getMappenavn() {
        return mappenavn;
    }

    public void setMappenavn(String mappenavn) {
        this.mappenavn = mappenavn;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
