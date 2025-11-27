package com.hallvardlaerum.libs.ui;

public enum ViewCRUDStatusEnum {

    UROERT("Urørt","Vinduet er akkurat åpnet, og ingenting er valgt. Redigeringsområdet er inaktivert og entitity er null"),
    NY("Ny","Bruker har akkurat klikket 'Ny'. Entity har uuid, men ikke noe mer."),
    POST_KAN_REDIGERES("Post kan redigeres","Bruker har klikket på en rad i grid, og entiteten vises i redigeringsområdet."),
    ER_SLETTET("Post er slettet","Bruker har akkurat klikket 'Slett'. Entiteten er fjernet fra grid, slettet og satt til null. Redigeringsområdet er inaktivt.");

    private String tittel;
    private String beskrivelse;

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

    ViewCRUDStatusEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }
}
