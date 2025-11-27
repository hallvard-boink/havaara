package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.filerogopplasting.FilopplastingsEgnet;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;

public interface BildeRedigeringsomraadeAktig<Bildeklasse extends BildeentitetAktig, Forelderklasse extends EntitetAktig>
    extends RedigeringsomraadeAktig<Bildeklasse>, FilopplastingsEgnet {

    void initier(EntitetserviceAktig<Bildeklasse> bildeservice, EntitetserviceAktig<Forelderklasse> forelderService, Boolean erFrittstaaende);
    void setBildeentitet(Bildeklasse bildeentitet);
    Bildeklasse getBildeentitet();

}
