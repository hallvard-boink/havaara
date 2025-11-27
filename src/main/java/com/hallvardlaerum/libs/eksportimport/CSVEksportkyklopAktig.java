package com.hallvardlaerum.libs.eksportimport;

import java.util.ArrayList;

public interface CSVEksportkyklopAktig {

    void eksporterArrayListTilTekst(String strEntitetsnavn, ArrayList alObjekter);
    void setDelimiter(String delimiter);
    //public <T> void velgImportfilOgKjoerImport(EntitetserviceAktig entitetserviceAktig)
}
