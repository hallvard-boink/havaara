package com.hallvardlaerum.libs.eksportimport;

import com.hallvardlaerum.libs.database.EntitetAktig;

import java.util.ArrayList;

public interface CSVKonverteringsAktig {

    EntitetAktig konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler);

}
