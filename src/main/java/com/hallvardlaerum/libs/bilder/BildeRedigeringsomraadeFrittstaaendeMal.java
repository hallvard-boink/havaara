package com.hallvardlaerum.libs.bilder;

import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;



/**
 * Denne klassen virker som en wrapper rundt standard bilderedigeringsområde (som gir feltene i MultiBildeKomponent),
 * slik at vi kan legge til denne klassen i et View, som et vanlig redigeringsområde
 *
 * @param <Bildeklasse>
 * @param <Forelderklasse>
 */
public abstract class BildeRedigeringsomraadeFrittstaaendeMal<Bildeklasse extends BildeentitetAktig<Forelderklasse>, Forelderklasse extends EntitetAktig>
        extends RedigeringsomraadeMal<Bildeklasse> implements BildeRedigeringsomraadeFrittstaaendeAktig<Bildeklasse,Forelderklasse>
{

    private EntitetserviceAktig<Bildeklasse,?> bildeservice;
    private EntitetserviceAktig<Forelderklasse,?> forelderservice;
    private BildeRedigeringsomraadeMal<Bildeklasse,Forelderklasse> bildeRedigeringsomraade;

    public BildeRedigeringsomraadeFrittstaaendeMal() {
        super();
    }

    @Override
    public void initier(EntitetserviceAktig<Bildeklasse,?> bildeservice, EntitetserviceAktig<Forelderklasse,?> forelderservice) {
        this.bildeservice = bildeservice;
        this.forelderservice = forelderservice;

        if (bildeRedigeringsomraade==null) {
           bildeRedigeringsomraade = new BildeRedigeringsomraadeMal<>();
           bildeRedigeringsomraade.initier(this.bildeservice, this.forelderservice, true);
           setSizeFull();
           this.add(bildeRedigeringsomraade);
        }
    }

    @Override
    public BildeRedigeringsomraadeAktig<Bildeklasse,Forelderklasse> hentBilderedigeringsomraade(){
        return bildeRedigeringsomraade;
    }



}
