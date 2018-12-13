package suche;


import de.fh.heuristicalSearch.HeuristicSearch;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanTileType;
import de.fh.util.Vector2;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by daniel on 22.09.16.
 * Generische Klasse für unsere Suchalgorithmen
 */
public abstract class Suche {

    public enum Suchstrategie{TIEFENSUCHE, BREITENSUCHE, DIJKSTAR, BESTENSUCHE, ASTERN}

    private Knoten zielKnoten;
    private PacmanPercept pacmanPercept;

     //In der Openlist befinden sich die zu expandierenden Knoten
    protected List<Knoten> openList;

    //In der Closelist befinden sich die bereits expandierenten Knoten als Hashwert um Loops verhindern
    protected HashSet<Integer> closedList;

    private int countSysout = 0;


    public Suche(PacmanPercept pacmanPercept, Knoten zielKnoten){
        this.zielKnoten = zielKnoten;
        this.pacmanPercept = pacmanPercept;

        openList = new LinkedList<>();
        closedList = new HashSet<Integer>();
    }

    /**
     * Ist die Suche fündig geworden, gibt die start-Methode den gefundenen Zielknoten zurück,
     * über den man sich dann wiederum die entsprechenden Actions (vom Start zum bis zum Ziel),
     * über eine entsprechende Methode, holen kann
     *
     * @return Ziel Knoten
     * */
     public Knoten start(){
         //Baue den Baum gemäß gewünschter Suche auf

         if (this.zielKnoten == null || this.pacmanPercept == null) {
             throw new NullPointerException("Ungültiger Zielzustand");
         }


         //Erzeuge Wurzelknoten
         this.fuegeKnotenEin(new Knoten(pacmanPercept.getView(), pacmanPercept.getPosition()));


         //Solange noch Expansionskandidaten vorhanden (Mindestens die Wurzel)
         while (!openList.isEmpty()) {
        	 
             //Es wird *immer* der erste Knoten aus der Openlist entnommen
             //Die Sortierung der Openlist bestimmt die Suche bzw. Ihr :-)
             Knoten expansionsKandidat = this.openList.remove(0);
             //Wird ein Knoten aus der Openlist entfernt landet
             //dieser sofort in der Closelist, damit dieser nicht noch einmal
             //expandiert wird (wir wollen keine loops im Baum!)
             this.closedList.add(expansionsKandidat.hashCode());

             //Schaue ob Knoten Ziel ist
             if (expansionsKandidat.isZiel(this.zielKnoten)) {
                 //Kandidat entspricht dem geünschten Zielzustand
                 Knoten loesungsKnoten = expansionsKandidat;
                 loesungsKnoten.berechnePacmanActions();
                 return loesungsKnoten;
             } else {
                 //Ist nicht gleich dem Zielzustand, also expandiere nächsten Knoten
                 expandiereKnoten(expansionsKandidat);

             }
         }

         //Keine Lösung gefunden
         return null;
     }

    private void expandiereKnoten(Knoten vorgaenger) {
        /**
         * Die Nachfolgerknoten werden der Reihe nach in die Openlist
         * verschoben. Bei dieser Reihenfolge wird beim nächsten expandieren
         * immer der nördliche, dann der östliche, usw. angeschaut
         */

        // West
        berechneNachfolger(PacmanAction.GO_WEST, vorgaenger);

        // South
        berechneNachfolger(PacmanAction.GO_SOUTH, vorgaenger);

        // East
        berechneNachfolger(PacmanAction.GO_EAST, vorgaenger);

        // Nord
        berechneNachfolger(PacmanAction.GO_NORTH, vorgaenger);


        if(countSysout % 100 ==  0) {
            System.out.println("o:" + openList.size() + "|" + "c:" + closedList.size());
        }
        countSysout++;

    }

    private void berechneNachfolger(PacmanAction bewegungsRichtung, Knoten vorgaenger) {
        //Ist die neue Postion eine Wandposition kann man sich das Erzeugen
        //des neuen Knoten und das Prüfen ob er sich schon in der closedList enthalten ist sparen
        Vector2 pos = vorgaenger.berechneNeuePosition(vorgaenger.getPos(), bewegungsRichtung);
        if (vorgaenger.getView()[pos.getX()][pos.getY()] == PacmanTileType.WALL)
            return;

        //Erzeuge Nachfolgerknoten nach gewünschter Bewegungsrichtung
        Knoten nachfolger = new Knoten(vorgaenger, bewegungsRichtung);


        //Durchsuche Closelist ob es diesen Zustand (Zustand der Welt und Pacman-Position) schon mal gab
        if (closedList.contains(nachfolger.hashCode()))
            //Zustand ist gleich, also nicht erneut in die Openlist aufnehmen (sonst Loop!)
        	return;

        //Knoten wird gemaess der Suchstrategie bewertet
    	if (this instanceof HeuristicSearch)
    		((HeuristicSearch)this).bewerteKnoten(nachfolger);
        //Es ist ein gültiger Nachfolgezustand, also in die Openlist
        fuegeKnotenEin(nachfolger);
    }




    /**
     * Zu implementierende Funktion für das  Einfügen eines Knoten in die Openlist
     *
     * @param expansionsKandidat
     */
    public abstract void fuegeKnotenEin(Knoten expansionsKandidat);


}
