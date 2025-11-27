package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;

public interface BildeRedigeringsomraadeFrittstaaendeAktig<Bildeklasse extends BildeentitetAktig, Forelderklasse extends EntitetAktig> {

    void initier(EntitetserviceAktig<Bildeklasse> bildeservice, EntitetserviceAktig<Forelderklasse> forelderservice);
    BildeRedigeringsomraadeAktig<Bildeklasse,Forelderklasse> hentBilderedigeringsomraade();

}
