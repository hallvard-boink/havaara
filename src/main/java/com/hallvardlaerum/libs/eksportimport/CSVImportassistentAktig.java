package com.hallvardlaerum.libs.eksportimport;

import com.hallvardlaerum.libs.database.EntitetAktig;

import java.util.ArrayList;

public interface CSVImportassistentAktig {

    void forberedImport();

    EntitetAktig konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler);

    void ryddOppEtterImport();

}
