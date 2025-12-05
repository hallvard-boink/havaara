package com.hallvardlaerum.libs.ui;

import com.hallvardlaerum.libs.database.EntitetAktig;

public interface RedigeringsdialogAktig<Entitet extends EntitetAktig> {
    void visDialog(Entitet entitet);
}
