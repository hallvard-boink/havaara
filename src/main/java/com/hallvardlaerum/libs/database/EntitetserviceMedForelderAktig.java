package com.hallvardlaerum.libs.database;

import java.lang.reflect.Field;

public interface EntitetserviceMedForelderAktig<EntitetKlasse extends EntitetMedForelderAktig, ForelderKlasse extends EntitetAktig> extends EntitetserviceAktig<EntitetKlasse>{

    //TODO: Er det behov for Class forelderklasse? Fjernes?

    void initier(Class<ForelderKlasse> forelderklasse, EntitetserviceAktig<ForelderKlasse> forelderentitetService);

    EntitetKlasse opprettEntitetMedForelder();

    void oppdaterForelderVedImport(Object o, Field field, String s);

    //TODO: Skal denne slettes?
    String hentForelderFeltNavn();
}
