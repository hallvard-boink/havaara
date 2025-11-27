package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;

import java.time.LocalDateTime;
import java.util.UUID;

public class BildeTilVanligEntitetWrapper<Bildeklasse extends BildeentitetAktig> implements EntitetAktig {
    private Bildeklasse bildeEntitet;

    public BildeTilVanligEntitetWrapper(Bildeklasse bildeEntitet) {
        this.bildeEntitet = bildeEntitet;
    }

    @Override
    public UUID getUuid() {
        return bildeEntitet.getUuid();
    }

    @Override
    public void setUuid(UUID uuid) {
        bildeEntitet.setUuid(uuid);
    }

    @Override
    public LocalDateTime getOpprettetDatoTid() {
        return bildeEntitet.getOpprettetDatoTid();
    }

    @Override
    public void setOpprettetDatoTid(LocalDateTime opprettetDatoTid) {
        bildeEntitet.setOpprettetDatoTid(opprettetDatoTid);
    }

    @Override
    public LocalDateTime getRedigertDatoTid() {
        return bildeEntitet.getRedigertDatoTid();
    }

    @Override
    public void setRedigertDatoTid(LocalDateTime redigertDatoTid) {
        bildeEntitet.setRedigertDatoTid(redigertDatoTid);
    }

    @Override
    public String hentBeskrivendeNavn() {
        return bildeEntitet.hentBeskrivendeNavn();
    }
}
