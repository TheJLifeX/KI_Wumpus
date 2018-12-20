package de.fh.heuristicalSearch;

import de.fh.blanks.HunterWorld;
import de.fh.blanks.Point;
import de.fh.suche.Knoten;
import de.fh.suche.Suche;

public abstract class HeuristicSearch extends Suche {


    public HeuristicSearch(HunterWorld hunterWorld, Point zielPosition) {
        super(hunterWorld, zielPosition);
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
