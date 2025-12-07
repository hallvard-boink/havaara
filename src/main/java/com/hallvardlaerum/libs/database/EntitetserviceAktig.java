package com.hallvardlaerum.libs.database;

import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;


/**
 * Interface som sørger for at ulike serviceklasser for entiteter i Spring boot har samme metoder
 *
 * @param <Entitet> Entiteten som serviceklassen håndterer
 */
public interface EntitetserviceAktig<Entitet extends EntitetAktig,
        Repo extends JpaRepository<Entitet, UUID>&JpaSpecificationExecutor<Entitet>& RepositoryTillegg<Entitet>> {

    void initEntitetserviceMal(Class<Entitet> klasse, Repo repository);

    Stream<Entitet> finnEntiteterMedSpecification(int offset, int limit, EntityFilterSpecification<Entitet> entityFilterSpecification, Sort sortering);
    int tellAntallMedSpecification(int offset, int limit, EntityFilterSpecification<Entitet> entityFilterSpecification);
    int tellAntallMedSpecification();
    EntityFilterSpecification<Entitet> getEntityFilterSpecification();
    void setEntityFilterSpecification(EntityFilterSpecification<Entitet> entityFilterSpecification);


    ArrayList<Entitet> finnAlle();
    Entitet opprettEntitet();
    void lagre(Entitet entity);
    void lagreAlle(ArrayList<Entitet> alEntities);
    void slett(Entitet entity);
    void flush();
    void opprettTestdata();
    boolean slettAlle();
    Entitet finnEtterUUID(String uuid);

    /**
     * Entitetservicen konverterer streng hvis den generiske importrutinen ikke klarer det
     *
     * @param entitet
     * @param field
     * @param nyVerdi
     * @return
     */

    boolean behandleSpesialfeltVedImport(Object entitet, Field field, String nyVerdi, String importradString);
    boolean lagreEkstrafeltTilSenere(EntitetAktig o, String feltnavnString, String verdiString, String importradString);

    String hentEntitetsnavn();
    String hentVisningsnavn(Entitet entitet);

    ArrayList<Entitet> finnAlleRedigertDatoTidMellom(LocalDateTime fraDatoTid, LocalDateTime tilDatoTid);
    Entitet finnSistRedigert();

    RedigeringsomraadeAktig<Entitet> hentRedigeringsomraadeAktig();

    int konverterOffsetOgLimitTilPageNumber(int offsetInt, int limitInt);


}
