package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;

public interface RedigerEntitetDialogEgnet<Entitet extends EntitetAktig>  {

    void oppdaterEtterLagring(Entitet entitet);
}
