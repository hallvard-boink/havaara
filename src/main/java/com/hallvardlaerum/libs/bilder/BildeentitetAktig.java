package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetMedForelderAktig;
import com.hallvardlaerum.libs.database.RekkefoeolgeAktig;

public interface BildeentitetAktig<Forelderklasse extends EntitetAktig>
        extends EntitetMedForelderAktig<Forelderklasse>, RekkefoeolgeAktig, EntitetAktig {


    Boolean getErHovedbilde();
    void setErHovedbilde(Boolean erHovedbilde);

    String getFilnavn();
    void setFilnavn(String filnavn);

    String getTittel();
    void setTittel(String tittel);

    Double getStoerrelseIMb();
    void setStoerrelseIMb(Double stoerrelseIMb);

    Integer getHoeydeIPixler();
    void setHoeydeIPixler(Integer hoeydeIPixler);

    Integer getBreddeIPixler();
    void setBreddeIPixler(Integer breddeIPixler);

    String getBeskrivelse();
    void setBeskrivelse(String beskrivelse);

    Forelderklasse getForelder();
    void setForelder(Forelderklasse forelder);

}
