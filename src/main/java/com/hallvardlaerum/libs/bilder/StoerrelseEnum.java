package com.hallvardlaerum.libs.bilder;

public enum StoerrelseEnum {

    EKSTRA_LITEN("Ekstra liten (512 x 256)", 512, 256),
    LITEN("Liten (1024 x 512)",1024,512),
    MIDDELS("Middels (1536 x 1024)",1536,1024),
    STOR("Stor (2048 x 1024)", 2048, 1024),
    ORIGINAL("Original størrelse (ingen forminskning)",0,0);

    private String tittel;
    private Integer horisontalPixler;
    private Integer vertikalPixler;

    StoerrelseEnum(String tittel, Integer horisontalPixler, Integer vertikalPixler) {
        this.tittel = tittel;
        this.horisontalPixler = horisontalPixler;
        this.vertikalPixler = vertikalPixler;
    }

    public String getTittel() {
        return tittel;
    }

    public Integer getHorisontalPixler() {
        return horisontalPixler;
    }

    public Integer getVertikalPixler() {
        return vertikalPixler;
    }
}
