package de.fh.suche;

import de.fh.blanks.CellInfo;
import de.fh.blanks.CellType;
import de.fh.blanks.HunterWorld;
import de.fh.blanks.Point;
import de.fh.heuristicalSearch.HeuristicSearch;
import de.fh.wumpus.enums.HunterAction;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Based on the algorithm of daniel Created on 22.09.16. Generische Klasse für unsere Suchalgorithmen
 */
public abstract class Suche {

	public enum Suchstrategie {
		TIEFENSUCHE, BREITENSUCHE, DIJKSTAR, BESTENSUCHE, ASTERN
	}

	private Point zielPosition;
	
	private HunterWorld hunterWorld;

	// In der Openlist befinden sich die zu expandierenden Knoten
	protected List<Knoten> openList;

	// In der Closelist befinden sich die bereits expandierenten Knoten als Hashwert
	// um Loops verhindern
	protected HashSet<Integer> closedList;

	private int countSysout = 0;

	public Suche(HunterWorld hunterWorld, Point zielPosition) {
		this.zielPosition = zielPosition;
		this.hunterWorld = hunterWorld;
		
		CellInfo zielPostionCellInfo = hunterWorld.get(zielPosition.getX(), zielPosition.getY());
		if ( zielPostionCellInfo == null || zielPostionCellInfo.getType() == CellType.WALL) {
			throw new IllegalArgumentException("zielPostion liegt außerhalb der erreichbare Welt");
		}
		
		openList = new LinkedList<>();
		closedList = new HashSet<Integer>();
	}

	/**
	 * Ist die Suche fündig geworden, gibt die start-Methode die Liste von HunterActions
	 *  zurück,um vom Startposition der Hunter bis zur gewünschter zielPostion zu gehen.  
	 *
	 * @return 
	 */
	public LinkedList<HunterAction> start() {
		// Baue den Baum gemäß gewünschter Suche auf

		if (this.zielPosition == null || this.hunterWorld == null || this.zielPosition.getX() < 0 || this.zielPosition.getY() < 0) {
			throw new NullPointerException("Ungültiger Zielzustand oder HunterWorld ist leer");
		}

		// Erzeuge Wurzelknoten
		this.fuegeKnotenEin(new Knoten(hunterWorld.getView(), hunterWorld.getHunterPosition(), hunterWorld.getHunterDirection()));

		// Solange noch Expansionskandidaten vorhanden (Mindestens die Wurzel)
		while (!openList.isEmpty()) {

			// Es wird *immer* der erste Knoten aus der Openlist entnommen
			// Die Sortierung der Openlist bestimmt die Suche bzw. Ihr :-)
			Knoten expansionsKandidat = this.openList.remove(0);
			
			// Wird ein Knoten aus der Openlist entfernt landet
			// dieser sofort in der Closelist, damit dieser nicht noch einmal
			// expandiert wird (wir wollen keine loops im Baum!)
			this.closedList.add(expansionsKandidat.hashCode());

			// Schaue ob Knoten Ziel ist
			if (expansionsKandidat.isZiel(this.zielPosition)) {
				// Kandidat entspricht dem gewünschten Zielzustand ( also Hunter an der ZielPostion) 
				Knoten loesungsKnoten = expansionsKandidat;
//				System.out.println("\nDie Suche war Erfolgsreich!\n");
				return loesungsKnoten.berechneHunterActions();
			} else {
				// Ist nicht gleich dem Zielzustand, also expandiere nächsten Knoten
				expandiereKnoten(expansionsKandidat);
			}
		}

		// Keine Lösung gefunden
		return null;
	}

	private void expandiereKnoten(Knoten vorgaenger) {
		/**
		 * Die Nachfolgerknoten werden der Reihe nach in die Openlist verschoben.
		 * 
		 * TODO diese Reihenfolgen eventuell anpassen. Also für einen effizenten Suchalgorithmus.
		 */


		berechneNachfolger(vorgaenger, HunterAction.TURN_LEFT);
		
		berechneNachfolger(vorgaenger, HunterAction.TURN_RIGHT);
        berechneNachfolger(vorgaenger, HunterAction.GO_FORWARD);

		if (countSysout % 100 == 0) {
//			System.out.println("o:" + openList.size() + "|" + "c:" + closedList.size());
		}
		countSysout++;

	}

	private void berechneNachfolger(Knoten vorgaenger, HunterAction hunterAction) {
		// Ist die neue Postion eine Wandposition kann man sich das Erzeugen
		// des neuen Knoten und das Prüfen ob er sich schon in der closedList enthalten
		// ist sparen
		if (hunterAction == HunterAction.GO_FORWARD) { // Nur wenn der Hunter vorwärts geht, kann seine Position sich
														// verändern.
			Point neueHunterPosition = vorgaenger.berechneNeuePosition(vorgaenger.getPos(),
					vorgaenger.getHunterDirection(), hunterAction);

			CellInfo cellInfo = hunterWorld.get(neueHunterPosition.getX(), neueHunterPosition.getY());
			if (cellInfo == null || cellInfo.getType() == CellType.WALL)
				return;
		}

		// Erzeuge Nachfolgerknoten nach gewünschter Bewegungsrichtung
		Knoten nachfolger = new Knoten(vorgaenger, hunterAction);

		// Durchsuche Closelist ob es diesen Zustand (Zustand der Welt) schon mal gab
		if (closedList.contains(nachfolger.hashCode()))
			// Zustand ist gleich, also nicht erneut in die Openlist aufnehmen (sonst Loop!)
			return;

		// Knoten wird gemaess der Suchstrategie bewertet
		if (this instanceof HeuristicSearch)
			((HeuristicSearch) this).bewerteKnoten(nachfolger);
		// Es ist ein gültiger Nachfolgezustand, also in die Openlist
		fuegeKnotenEin(nachfolger);
	}

	/**
	 * Zu implementierende Funktion für das EinfÃ¼gen eines Knoten in die Openlist
	 *
	 * @param expansionsKandidat
	 */
	public abstract void fuegeKnotenEin(Knoten expansionsKandidat);

}
