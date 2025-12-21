package com.hallvardlaerum.libs.eksportimport;

import com.hallvardlaerum.libs.database.EntitetAktig;

import java.util.ArrayList;

public interface CSVImportassistentAktig<Entitet extends EntitetAktig> {

    void forberedImport();

    Entitet konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler);

    void ryddOppEtterImport();

    //        return entitet;
    //    }


    void lesInnFeltnavnogCeller(ArrayList<String> feltnavnCSVArrayList, String[] celler);

    String hentImportRadString();

    Integer parseInt(String integerString);

    String hentVerdier(boolean setterInnLinjeskift, boolean setterInnFeltnavn, String... feltnavnene);

    String hentVerdi(String feltnavn);

    String hentFeltnavnCSVString();

    Integer finnPosisjon(String feltnavn);

    String hentcellerString();
}
