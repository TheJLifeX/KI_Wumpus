package heuristicalSearch;

import de.fh.pacman.PacmanPercept;
import de.fh.suche.Knoten;
import de.fh.suche.Suche;

public abstract class HeuristicSearch extends Suche {


    public HeuristicSearch(PacmanPercept pacmanPercept, Knoten zielKnoten) {
        super(pacmanPercept, zielKnoten);
    }

    /**
     * Zu implementierende Funktion für das Bewerten eines Knoten
     *
     * @param expansionsKandidat
     */
    public abstract void bewerteKnoten(Knoten expansionsKandidat);


    /**
     * Konkrete Implentierung des Einfügens eines Knoten in
     * die Openlist der entsprechenden Suche
     *
     * @param expansionsKandidat
     */
    public abstract void fuegeKnotenEin(Knoten expansionsKandidat);

}
