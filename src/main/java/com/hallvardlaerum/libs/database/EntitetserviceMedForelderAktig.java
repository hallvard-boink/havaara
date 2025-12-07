package com.hallvardlaerum.libs.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.Field;
import java.util.UUID;

public interface EntitetserviceMedForelderAktig
        <EntitetKlasse extends EntitetMedForelderAktig,
        ForelderKlasse extends EntitetAktig,
        Repo extends JpaRepository<EntitetKlasse, UUID> & JpaSpecificationExecutor<EntitetKlasse> & RepositoryTillegg<EntitetKlasse>,
        RepoForelder extends JpaRepository<ForelderKlasse, UUID> & JpaSpecificationExecutor<ForelderKlasse> & RepositoryTillegg<ForelderKlasse>>
        extends EntitetserviceAktig<EntitetKlasse,Repo>{

    //TODO: Er det behov for Class forelderklasse? Fjernes?

    void initierEntitetserviceMedForelderMal(Class<ForelderKlasse> forelderklasse, EntitetserviceAktig<ForelderKlasse, RepoForelder> forelderentitetService);

    EntitetKlasse opprettEntitetMedForelder();

    void oppdaterForelderVedImport(Object o, Field field, String s);

    //TODO: Skal denne slettes?
    String hentForelderFeltNavn();
}
