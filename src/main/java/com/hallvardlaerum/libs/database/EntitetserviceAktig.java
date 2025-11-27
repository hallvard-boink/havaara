package com.hallvardlaerum.libs.database;

import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;


/**
 * Interface som sørger for at ulike serviceklasser for entiteter i Spring boot har samme metoder
 *
 * @param <T> Entiteten som serviceklassen håndterer
 */
public interface EntitetserviceAktig<T extends EntitetAktig> {

    Stream<T> finnEntiteterMedSpecification(int offset, int limit, EntityFilterSpecification<T> entityFilterSpecification, Sort sortering);
    int tellAntallMedSpecification(int offset, int limit, EntityFilterSpecification<T> entityFilterSpecification);
    int tellAntallMedSpecification();
    EntityFilterSpecification<T> getEntityFilterSpecification();
    void setEntityFilterSpecification(EntityFilterSpecification<T> entityFilterSpecification);


    ArrayList<T> finnAlle();
    T opprettEntitet();
    void lagre(T entity);
    void lagreAlle(ArrayList<T> alEntities);
    void slett(T entity);
    void flush();
    void opprettTestdata();
    boolean slettAlle();
    T finnEtterUUID(String uuid);

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
    String hentVisningsnavn(T entitet);

    ArrayList<T> finnAlleRedigertDatoTidMellom(LocalDateTime fraDatoTid, LocalDateTime tilDatoTid);
    T finnSistRedigert();

    RedigeringsomraadeAktig<T> hentRedigeringsomraadeAktig();

    int konverterOffsetOgLimitTilPageNumber(int offsetInt, int limitInt);


}
