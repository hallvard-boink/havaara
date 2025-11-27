package com.hallvardlaerum.libs.filerogopplasting;

import com.vaadin.flow.server.streams.UploadMetadata;

public interface LastOppFilDialogAktig {

    void oppdaterErUnik();
    void oppdaterErPasseStor();

    UploadMetadata getOpplastetfilMetadata();
    byte[] getOpplastetfilData();

    String getFilnavnString();
    void setFilnavnString(String filnavnString);

    Filkategori getFilkategori();
    void close();
    FilopplastingsEgnet getView();
    void settSkalKomprimeresOgOppdaterLayout(Boolean skalKomprimeres);
}
