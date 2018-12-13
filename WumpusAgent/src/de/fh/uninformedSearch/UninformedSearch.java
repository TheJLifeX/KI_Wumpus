package uninformedSearch;

import de.fh.pacman.PacmanPercept;
import de.fh.suche.Knoten;
import de.fh.suche.Suche;

public abstract class UninformedSearch extends Suche {


    public UninformedSearch(PacmanPercept pacmanPercept, Knoten zielKnoten) {
        super(pacmanPercept, zielKnoten);
    }

    /**
     * Konkrete Implentierung des Einfügens eines Knoten in
     * die Openlist der entsprechenden Suche
     *
     * @param expansionsKandidat
     */
    public abstract void fuegeKnotenEin(Knoten expansionsKandidat);

}
