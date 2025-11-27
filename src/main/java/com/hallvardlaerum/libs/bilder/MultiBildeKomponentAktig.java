package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;

import java.util.ArrayList;

public interface MultiBildeKomponentAktig<BildeentitetKlasse extends BildeentitetAktig, ForelderKlasse extends EntitetAktig> {

    void init(MultiBildeKomponentMal.VisningEnum visningEnum,
              EntitetserviceAktig<BildeentitetKlasse> bildeentitetservice,
              BildeRedigeringsomraadeAktig<BildeentitetKlasse, ForelderKlasse> bildeRedigeringsomraade,
              EntitetserviceAktig<ForelderKlasse> forelderentitetservice,
              RedigeringsomraadeAktig<ForelderKlasse> forelderRedigeringsomraade);
    void visBildeNr (Integer plassNr);
    void oppdaterForFremfunnetForelderentitet(ArrayList<BildeentitetKlasse> bilder);

}
