package com.hallvardlaerum.libs.feiloglogging;

public interface LoggekyklopAktig {

    void forberedTilImportloggTilFil();

    void avsluttImportloggTilFil();

    void settNivaaINFO();

    void settNivaaDEBUG();

    void settNivaaADVARSEL();

    void settNivaaFEIL();

    void settNivaaKRITISK_FEIL();

    void ikkeVisNotifikasjonerTilBruker();

    void visNotifikasjonerTilBruker();

    void loggKRITISK_FEIL(String strMelding);

    void loggFEIL(String strMelding);

    void loggADVARSEL(String strMelding);

    void loggDEBUG(String strMelding);

    void loggINFO(String strMelding);

    void loggTEST(String strMelding);
}
