package com.hallvardlaerum.libs.database;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.feiloglogging.LoggekyklopAktig;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


public abstract class EntitetserviceMal<Entitet extends EntitetAktig,
        Repo extends JpaRepository<Entitet,UUID> & JpaSpecificationExecutor<Entitet> & RepositoryTillegg<Entitet>>
        implements EntitetserviceAktig<Entitet, Repo> {
    private Repo repository;
    private Class<Entitet> klasse;
    private EntityFilterSpecification<Entitet> entityFilterSpecification;



    @Override
    public void initEntitetserviceMal(Class<Entitet> klasse, Repo repository) {
        this.repository = repository;
        this.klasse = klasse;
    }


    @Override
    public Stream<Entitet> finnEntiteterMedSpecification(int offset, int limit,
             EntityFilterSpecification<Entitet> entityFilterSpecification, Sort sortering)
    {
        this.entityFilterSpecification = entityFilterSpecification;
        int pageInteger = konverterOffsetOgLimitTilPageNumber(offset,limit);
        if (this.entityFilterSpecification !=null) {
            PageRequest pageRequest = PageRequest.of(pageInteger,limit,sortering);
            return hentRepository().findAll(this.entityFilterSpecification, pageRequest).stream();
        } else {
            return hentRepository().findAll(PageRequest.of(pageInteger, limit, sortering)).stream();
        }
    }


    @Override
    public int tellAntallMedSpecification(int offset, int limit, EntityFilterSpecification<Entitet> entityFilterSpecification) {
        this.entityFilterSpecification = entityFilterSpecification;
        return tellAntallMedSpecification();
    }

    @Override
    public int tellAntallMedSpecification(){
        if (this.entityFilterSpecification !=null) {
            return hentRepository().findAll(this.entityFilterSpecification).size();
        } else {
            return hentRepository().findAll().size();
        }
    }


    public Repo hentRepository(){
        return this.repository;
    }

    public EntitetserviceMal() {
    }

    @Override
    public int konverterOffsetOgLimitTilPageNumber(int offsetInt, int limitInt) {
        int pageInt = offsetInt / limitInt;
        return pageInt;

    }

    @Override
    public ArrayList<Entitet> finnAlle() {
        return new ArrayList<>(repository.findAll());
    }


    public Entitet leggTilUUID(Entitet entitet) {
        entitet.setUuid(UUID.randomUUID());
        return entitet;
    }

    @Override
    public void lagre(Entitet entitet) {
        repository.saveAndFlush(entitet);
    }

    @Override
    public void lagreAlle(List<Entitet> alEntities) {
        repository.saveAllAndFlush(alEntities);
    }

    @Override
    public void slett(Entitet entitet) {
        repository.delete(entitet);
        repository.flush();
    }

    @Override
    public void flush() {
        repository.flush();
    }

    /**
     * En jalla-utgave er lagt inn her, slik at utvikler ikke MÅ implementere den
     */
    @Override
    public void opprettTestdata() {
        repository.save(opprettEntitet());
        repository.save(opprettEntitet());
        repository.save(opprettEntitet());
        repository.save(opprettEntitet());
        repository.flush();
    }

    @Override
    public boolean slettAlle() {
        try {
            repository.deleteAll();
            repository.flush();
        } catch (Exception e) {
            Loggekyklop.bruk().loggADVARSEL("Klarte ikke å slette alle i " + hentEntitetsnavn());
            return false;
        }
        return true;
    }

    @Override
    public Entitet finnEtterUUID(String s) {
        if (s==null || s.isEmpty()) {
            return null;
        }

        Optional<Entitet> optional = repository.findById(UUID.fromString(s));
        if (optional.isEmpty()) {
            Loggekyklop.bruk().loggADVARSEL("Fant ikke entiteten " + klasse.getName() + " med UUID " + s );
            return null;
        } else {
            return optional.get();
        }
    }

    @Override
    public boolean lagreEkstrafeltTilSenere(EntitetAktig entitet, String feltnavnString, String verdiStreng, String importradString) {
        return false;
    }


    @Override
    public String hentEntitetsnavn() {
        return klasse.getSimpleName();
    }


    @Override
    public String hentVisningsnavn(Entitet entitet) {
        return entitet.hentBeskrivendeNavn();
    }

    @Override
    public ArrayList<Entitet> finnAlleRedigertDatoTidMellom(LocalDateTime fraDatoTid, LocalDateTime tilDatoTid) {
        return new ArrayList<>(repository.findByRedigertDatoTidBetween(fraDatoTid, tilDatoTid));
    }

    @Override
    public Entitet finnSistRedigert(){
        return repository.findFirstByOrderByRedigertDatoTidDesc();
    }

    @Override
    public boolean behandleSpesialfeltVedImport(Object entitet, Field field, String nyVerdi, String importradString) {
        return false;
    }



    // === GETTERS AND SETTERS
    @Override
    public EntityFilterSpecification<Entitet> getEntityFilterSpecification() {
        return entityFilterSpecification;
    }

    @Override
    public void setEntityFilterSpecification(EntityFilterSpecification<Entitet> entityFilterSpecification) {
        this.entityFilterSpecification = entityFilterSpecification;
    }


}
