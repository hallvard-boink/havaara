package com.hallvardlaerum.libs.ui;



import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.database.RepositoryTillegg;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Denne kan brukes på alle abstrakte View-klasser, for eksempel MasterDetailViewmal
 *
 * @param <Entitet>
 */
public interface ViewmalAktig<Entitet extends EntitetAktig,
        Repo extends JpaRepository<Entitet, UUID> & JpaSpecificationExecutor<Entitet> & RepositoryTillegg<Entitet>> {

    SubMenu hentVerktoeySubMeny();

    ViewCRUDStatusEnum getViewCRUDStatus();
    void setViewCRUDStatus(ViewCRUDStatusEnum viewCRUDStatus);

    HorizontalLayout hentKnapperadSoekefelt();
    HorizontalLayout hentKnapperadRedigeringsfelt();
    Button hentNyButton();
    Button hentLagreButton();
    Button hentSlettButton();

    void opprettLayout(EntitetserviceAktig<Entitet, Repo> entitetserviceAktig, RedigeringsomraadeAktig<Entitet> redigeringsomraade);
    void opprettLayout(EntitetserviceAktig<Entitet, Repo> entitetserviceAktig, RedigeringsomraadeAktig<Entitet> redigeringsomraade, SplitLayout.Orientation orientering);
    void opprettLayout(EntitetserviceAktig<Entitet, Repo> entitetserviceAktig, RedigeringsomraadeAktig<Entitet> redigeringsomraade, SplitLayout.Orientation orientering, Double splitPositionDouble);
    void opprettLayout(EntitetserviceAktig<Entitet, Repo> entitetserviceAktig,
                       RedigeringsomraadeAktig<Entitet> redigeringsomraade,
                       SplitLayout.Orientation orientering,
                       Double splitPositionDouble,
                       GridInnholdsTypeEnum gridInnholdsTypeEnum);


    <C extends Component> C leggTilFilterfelt(Integer cellIndex, C component, String placeholder);
    HeaderRow getHeaderRowFilterfelter(); // tatt med i tilfelle utvikler trenger mer avansert tilpasning

    void settFilter();

    void instansOpprettGrid();
    void instansOpprettFilterFelter();
    void instansTilpassNyopprettetEntitet();
    void instansAktiverKnapperadRedigeringsfelt(Boolean aktiver);

    void oppdaterSoekeomraadeFinnAlleRader();

    void oppdaterAntallRaderNederstIGrid();

    void oppdaterSoekeomraade_finnAlle(Entitet entitet);
    void oppdaterSoekeomraadeEtterRedigeringAvEntitet();

    void oppdaterSoekeomraadeOgRedigeringsomraadeMedNyEntitet(Entitet entitet);

    ViewCRUDStatusEnum getCRUDStatus();

    void oppdaterRedigeringsomraade();
    RedigeringsomraadeAktig<Entitet> hentRedigeringsomraadeAktig();

    void lagreEntitet();

    void initierCallbackDataProviderIGrid (
            @NotNull com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback<Entitet, String> fetchCallback,
            @NotNull com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback<Entitet, String> countCallback
    );

    H2 hentVindutittel();

}
