package com.hallvardlaerum.libs.database;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.TekstKyklop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.Field;
import java.util.UUID;

public abstract class EntitetserviceMedForelderMal
        <EntitetKlasse extends EntitetMedForelderAktig,
        ForelderKlasse extends EntitetMedBarnAktig,
        Repo extends JpaRepository<EntitetKlasse, UUID> & JpaSpecificationExecutor<EntitetKlasse> & RepositoryTillegg<EntitetKlasse>,
        RepoForelder extends JpaRepository<ForelderKlasse, UUID> & JpaSpecificationExecutor<ForelderKlasse> & RepositoryTillegg<ForelderKlasse>>
        extends EntitetserviceMal<EntitetKlasse, Repo>
        implements EntitetserviceMedForelderAktig<EntitetKlasse, ForelderKlasse, Repo, RepoForelder>

{
    private EntitetserviceAktig<ForelderKlasse, RepoForelder> forelderentitetservice;
    private Class<ForelderKlasse> forelderklasse;

    public EntitetserviceMedForelderMal() {
    }

    @Override
    public void initierEntitetserviceMedForelderMal(Class<ForelderKlasse> forelderklasse,
                                                    EntitetserviceAktig<ForelderKlasse, RepoForelder> forelderentitetService) {
        this.forelderentitetservice = forelderentitetService;
        this.forelderklasse = forelderklasse;
    }

    @Override
    public String hentForelderFeltNavn() {
        return forelderklasse.getName();
    }

    @Override
    public void oppdaterForelderVedImport(Object o, Field field, String s) {
        if (s==null || s.isEmpty()) {
            return;
        }

        if (!(o instanceof EntitetMedForelderAktig<?>)) {
            Loggekyklop.hent().loggFEIL("Objektet o har type " + o.getClass().getName() +
                    " som ikke ekstenderer EntitetMedForelderAktig. Avbryter");
            return;
        }
        EntitetKlasse entitet = (EntitetKlasse)o;
        try {
            if (field.getName().equalsIgnoreCase(forelderklasse.getSimpleName())) {
                String strUuid = TekstKyklop.hent().hentSisteIStrengMedDelimiter(s,"@");
                ForelderKlasse forelderFunnet = forelderentitetservice.finnEtterUUID(strUuid);
                if (forelderFunnet==null) {
                    Loggekyklop.hent().loggADVARSEL("Fant ikke forelderklasse " + forelderklasse.getName() +
                            " med uuid " + strUuid + " til entiteten " + entitet.hentBeskrivendeNavn());
                } else {
                    field.set(entitet,forelderFunnet);
                }
            }
        } catch (IllegalArgumentException e) {
                  Loggekyklop.hent().loggFEIL("Feltet \"" + field.getName() + "\" finnes ikke i entiteten Bil.");
              } catch (IllegalAccessException e) {
                  Loggekyklop.hent().loggFEIL("Cannot access field: " + field.getName());
              } catch (Exception e) {
                  Loggekyklop.hent().loggFEIL("An error occurred while updating the field: " + e.getMessage());
        }

    }

}
