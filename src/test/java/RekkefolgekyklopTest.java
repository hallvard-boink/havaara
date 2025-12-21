import com.hallvardlaerum.libs.database.RekkefoeolgeAktig;
import com.hallvardlaerum.libs.felter.Rekkefolgekyklop;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class RekkefolgekyklopTest {
    private ArrayList<RekkefoelgeObjekt> liste;

    //@Test
    public void flyttFremover() {
        byggListe();
        printListe();

        System.out.println("");
        System.out.println("Flytt A fremover:");
        RekkefoelgeObjekt objektA = liste.getFirst();
        Rekkefolgekyklop.hent().flyttFremover(objektA, liste);
        assert objektA.equals(liste.get(1));
        printListe();

        System.out.println("");
        System.out.println("Flytt A fremover:");
        Rekkefolgekyklop.hent().flyttFremover(objektA, liste);
        assert objektA.equals(liste.get(2));
        printListe();

        System.out.println("");
        System.out.println("Flytt A fremover:");
        Rekkefolgekyklop.hent().flyttFremover(objektA, liste);
        assert objektA.equals(liste.get(3));
        printListe();

        System.out.println("");
        System.out.println("Flytt A fremover (men skal ikke forbi sisteplass)");
        Rekkefolgekyklop.hent().flyttFremover(objektA, liste);
        assert objektA.equals(liste.get(3));
        printListe();

    }

    //@Test
    public void flyttBakover() {
        byggListe();
        printListe();

        System.out.println("");
        System.out.println("Flytt D bakover:");
        RekkefoelgeObjekt objektD = liste.getLast();
        Rekkefolgekyklop.hent().flyttBakover(objektD, liste);
        assert objektD.equals(liste.get(2));
        printListe();

        System.out.println("");
        System.out.println("Flytt D bakover:");
        Rekkefolgekyklop.hent().flyttBakover(objektD, liste);
        assert objektD.equals(liste.get(1));
        printListe();

        System.out.println("");
        System.out.println("Flytt D bakover:");
        Rekkefolgekyklop.hent().flyttBakover(objektD, liste);
        assert objektD.equals(liste.getFirst());
        printListe();

        System.out.println("");
        System.out.println("Flytt D bakover (men skal ikke forbi førsteplass)");
        Rekkefolgekyklop.hent().flyttBakover(objektD, liste);
        assert objektD.equals(liste.getFirst());
        printListe();

    }

    //@Test
    public void flyttFoerst() {
        byggListe();
        printListe();
        System.out.println("Flytt objektC først");
        RekkefoelgeObjekt objektC = liste.get(2);
        Rekkefolgekyklop.hent().flyttFoerst(objektC, liste);;
        printListe();
        assert objektC.equals(liste.getFirst());
    }

    //@Test
    public void flyttSist() {
        byggListe();
        printListe();
        System.out.println("Flytt objektC sist");
        RekkefoelgeObjekt objektC = liste.get(2);
        Rekkefolgekyklop.hent().flyttSist(objektC, liste);;
        printListe();
        assert objektC.equals(liste.getLast());
    }

    private void printListe(){
        System.out.println("Navn|Rekkefølge|");
        System.out.println("---------------");
        for (RekkefoelgeObjekt o:liste){
            System.out.println("| " + o.navn + " |   " +o.rekkefoelge + "     |" );
        }
    }

    private void byggListe(){
        liste = new ArrayList<>();
        liste.add(new RekkefoelgeObjekt(10,"A"));
        liste.add(new RekkefoelgeObjekt(20,"B"));
        liste.add(new RekkefoelgeObjekt(30,"C"));
        liste.add(new RekkefoelgeObjekt(40,"D"));
    }


    private class RekkefoelgeObjekt implements RekkefoeolgeAktig {
        Integer rekkefoelge = 0;
        String navn;

        public RekkefoelgeObjekt(Integer rekkefoelge, String navn) {
            this.rekkefoelge = rekkefoelge;
            this.navn = navn;
        }

        @Override
        public Integer getRekkefoelge() {
            return rekkefoelge;
        }

        @Override
        public void setRekkefoelge(Integer rekkefoelge) {
            this.rekkefoelge = rekkefoelge;
        }

        public String getNavn() {
            return navn;
        }

        public void setNavn(String navn) {
            this.navn = navn;
        }
    }
}
