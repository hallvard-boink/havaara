package com.hallvardlaerum.libs.database;

public interface EntitetMedForelderAktig<Forelderklasse extends EntitetAktig> extends EntitetAktig{

    void setForelder(Forelderklasse forelder);
    Forelderklasse getForelder();

}
