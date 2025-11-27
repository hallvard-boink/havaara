package com.hallvardlaerum.libs.filerogopplasting;

/**
 * Implementeres av Views som skal bruke Filkyklop sin opplastingsfunksjon (som krever callback)
 */
public interface FilopplastingsEgnet {

    void lagringEtterOpplastingEllerValgAvFil(String nyttFilnavnString);
    String getFilnavn();
    Filkategori getFilkategori();

}
