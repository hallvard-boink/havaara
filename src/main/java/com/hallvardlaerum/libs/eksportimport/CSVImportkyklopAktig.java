package com.hallvardlaerum.libs.eksportimport;



import com.hallvardlaerum.libs.database.EntitetserviceAktig;

import java.util.ArrayList;

public interface CSVImportkyklopAktig {


    boolean importerFraArrayListMedStrenger(ArrayList<String> arrayListInnhold);
    String getDelimiter();

    void velgImportfilOgKjoerImport(EntitetserviceAktig<?> entitetserviceAktig );
    String hentUtKlassenavn(String strVerdi);

}
