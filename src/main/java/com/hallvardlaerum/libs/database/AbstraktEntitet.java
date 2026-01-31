package com.hallvardlaerum.libs.database;

import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Brukes i mine vaadin-apper for å ha konsistent bruk av uuid som id
 * Bruker ikke @GeneratedValue(strategy = GenerationType.UUID), fordi
 * det skaper problemer når vi skal importere rader med uuid, f.eks. for å
 * rekonstruere innhold i tabeller som har relasjonskoblinger. I stedet må vi
 * opprette uuid selv.
 *
 * Obs! Tidligere utgaver medførte at MariaDB la alle dataene i en tabell.
 * Det var ikke bærekraftig, og ga noen underlige bugs. Tidligere utgave var merket @Entity,
 * og hadde sin egen @Id med UUID.
 *
 * I denne utgaven skal @Id legges i hver konkrete
 * entity, og bruken av uuid antydes med abstrakte metoder som må implementeres i hver
 * konkrete entitet.
 *
 *
 */

@MappedSuperclass
public abstract class AbstraktEntitet implements EntitetAktig {

    @Id
    @SkalEksporteres
    private UUID uuid;

    @CreationTimestamp
    @Column(updatable=false)
    private LocalDateTime opprettetDatoTid;

    @UpdateTimestamp
    private LocalDateTime redigertDatoTid;

    @Override
    public String toString(){
        return this.getClass().getName() + "@" + getUuid().toString();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public LocalDateTime getOpprettetDatoTid() {
        return opprettetDatoTid;
    }

    @Override
    public void setOpprettetDatoTid(LocalDateTime opprettetDatoTid) {
        this.opprettetDatoTid = opprettetDatoTid;
    }

    @Override
    public LocalDateTime getRedigertDatoTid() {
        return redigertDatoTid;
    }

    @Override
    public void setRedigertDatoTid(LocalDateTime redigertDatoTid) {
        this.redigertDatoTid = redigertDatoTid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstraktEntitet)) return false;
        AbstraktEntitet other = (AbstraktEntitet) o;
        return Objects.equals(this.uuid, other.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String hentEksportID() {
        if (uuid!=null) {
            return "UUID:" + uuid;
        } else {
            return hentBeskrivendeNavn();
        }
    }



}
