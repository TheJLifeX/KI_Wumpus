package de.fh.heuristicalSearch;

import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.suche.Knoten;
import de.fh.util.Vector2;

import java.util.Comparator;

public class AStern extends HeuristicSearch{

    public AStern(PacmanPercept pacmanPercept, Knoten zielKnoten){
        super(pacmanPercept, zielKnoten);
    }


    /**
     * Konkrete Implentierung des Bewerten eines Knotens
     * gemäß der entsprechenden Suche
     *
     * @param expansionsKandidat
     */
    @Override
    public void bewerteKnoten(Knoten expansionsKandidat) {

        float schaetzwert = 0f, pfadkosten = 0f;

        // TODO AStern

        int numDots = 0;
        PacmanTileType[][] view = expansionsKandidat.getView();
        for (int i = 0; i < view.length; i++)
            for (int y = 0; y < view[0].length; y++)
                if (view[i][y] == PacmanTileType.DOT)
                    numDots++;

        schaetzwert = numDots;

        Knoten zeiger = expansionsKandidat.getVorgaenger();
        Vector2 pos = zeiger.getPos();
        while(zeiger != null)
        {
            int x = pos.getX();
            int y = pos.getY();
            if(zeiger.getView()[x][y] == PacmanTileType.EMPTY)
                pfadkosten++;

            zeiger = zeiger.getVorgaenger();
        }

        //setzt die bisherigen Pfadkosten zu dem Knoten
        expansionsKandidat.setPfadkosten(pfadkosten);
        //Setzt den richtigen Schätzwert für den Knoten
        expansionsKandidat.setSchaetzwert(schaetzwert);


    }


    /**
     * Konkrete Implentierung des Einfügens eines Knoten in
     * die Openlist bei der Tiefensuche
     *
     * @param expansionsKandidat
     */
    @Override
    public void fuegeKnotenEin(Knoten expansionsKandidat) {

        //TODO AStern

        //Implementiert openList.add(Index,exp) mit dem richtigen Index gemäß Suchstrategie
        openList.add(0, expansionsKandidat);

        openList.sort(new Comparator<Knoten>() {
            @Override
            public int compare(Knoten o1, Knoten o2) {
                return Float.compare(o1.getBewertung(), o2.getBewertung());
            }
        });

    }


}
