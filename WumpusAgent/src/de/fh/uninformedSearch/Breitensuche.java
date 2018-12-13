package uninformedSearch;

import de.fh.pacman.PacmanPercept;
import de.fh.suche.Knoten;

public class Breitensuche extends UninformedSearch{

    public Breitensuche(PacmanPercept pacmanPercept, Knoten zielKnoten){
        super(pacmanPercept, zielKnoten);
    }


    /**
     * Konkrete Implentierung des Einfügens eines Knoten in
     * die Openlist bei der Tiefensuche
     *
     * @param expansionsKandidat
     */
    @Override
    public void fuegeKnotenEin(Knoten expansionsKandidat) {

        // TODO Breitensuche

        System.out.println("test");

        //Implementiert openList.add(Index,exp), mit dem richtigen Index gemäß Suchstrategie
        openList.add(openList.size(), expansionsKandidat);
    }
}
