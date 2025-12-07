package com.hallvardlaerum.libs.eksportimport;



import com.hallvardlaerum.libs.database.EntitetserviceAktig;

import java.util.ArrayList;

public interface CSVImportmesterAktig {


    boolean importerFraArrayListMedStrenger(ArrayList<String> arrayListInnhold);
    String getDelimiter();

    CSVImportassistentAktig getCsvImportassistentAktig();

    void setCsvImportassistentAktig(CSVImportassistentAktig csvImportassistentAktig);

    String getLesCharsetString();

    void setLesCharsetString(String lesCharsetString);

    void velgImportfilOgKjoerImport(EntitetserviceAktig<?, ?> entitetserviceAktig );
    String hentUtKlassenavn(String strVerdi);

}
