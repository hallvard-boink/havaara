package com.hallvardlaerum.libs.eksportimport;

import java.io.File;
import java.util.ArrayList;

public interface ExcelEksportkyklopAktig {

    <T> void eksporterArrayListAvEntiteterSomXLS(ArrayList<T> entiteteneArrayList, String strFilnavn);

    void eksporterArrayListAvStrengerSomXLS(ArrayList<ArrayList<String>> radeneArrayList, String strFilnavn);
}
