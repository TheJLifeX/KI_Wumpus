package de.fh.suche;

import de.fh.blanks.CellInfo;
import de.fh.blanks.Direction;
import de.fh.blanks.Point;
import de.fh.wumpus.enums.HunterAction;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Based on the algorithm of daniel Created on 22.09.16. Klasse für unsere Suchalgorithmen
 */
public class Knoten {

	// Knoten haben bis auf die Wurzel Vorgänger
	protected Knoten vorgaenger;

	// Im diesem Knoten aktuelle Direction der Hunter
	private Direction hunterDirection;

	/**
	 * Knotenbewertung eines Knoten x nach f(x)=g(x)+h(x) mit g(x) die bisherigen
	 * Kosten vom Startknoten aus h(x) die geschÃ¤tzten Kosten von x bis zum
	 * Zielknoten
	 * 
	 * @return die heuristische Bewertung des Knoten x
	 */
	protected float pfadkosten = 0f;
	protected float schaetzwert = 0f;

	// Die Felderbelegung der (virtuellen) aktuellen Welt
	// allerdings ohne den Hunter (HunterPostion wird in hunterPosition gespeichert)
	private ArrayList<ArrayList<CellInfo>> view;

	// Die Felderbelegung wird zusätzlich als Stringarray gespeichert
	// um Knoten effizienter zu vergleichen
	private String sView;

	// Die Position der Hunter, während der Suche
	private Point hunterPosition = new Point(0, 0);

	// Konstruktor für den Wurzelknoten
	public Knoten(ArrayList<ArrayList<CellInfo>> view, Point hunterPosition, Direction hunterDirection) {

		// Der Wurzelknoten und Zielzustand hat keinen Vorgänger
		this.vorgaenger = null;

		// Die Wurzel kennt die aktuelle position der Hunter
		this.hunterPosition = hunterPosition;

		// Die Richtung der Hunter am Anfang des Suchalgorithmus
		this.hunterDirection = hunterDirection;

		// TODO ID:1 momentan ist die view für den Suchalgorithmus in einem Knoten irrelevant.
		// bleibt zuerst so.
		// Später wird man auch die view auch während des Suchalgorithmus aktualisiert.
		this.view = view;

		// Erzeuge view as String
		// Nur relevant für die Berechung der Hashcode
		sView = this.view.toString();

	}

	// Konstruktor für Nachfolger Knoten.
	public Knoten(Knoten vorgaenger, HunterAction action) {
		this.vorgaenger = vorgaenger;

		// TODO eventuell Erzeuge neues view auf Basis des Vorgängers
		// wie TODO ID:1
		this.view = vorgaenger.getView();

		// HunterDirection der Vorgaenger, wird eventuell in der Methode berechneNeuePostion aktualisiert
		this.hunterDirection = vorgaenger.getHunterDirection();
		
		// Die neue Position der Hunter auf Basis der Bewegungsrichtung
		// PS: In berechNeuePostion wird auch die neue hunterDirection berechnet.
		this.hunterPosition = berechneNeuePosition(vorgaenger.getPos(), vorgaenger.getHunterDirection(), action);

		// Erzeuge view as String
		// Nur relevant für die Berechung der Hashcode
		this.sView = this.view.toString();

	}

	// Kopieren von 2-dimensionalen dymanische Liste.
	// TODO ID:1  2-dimensionalen dymanische Liste  manuell kopieren.
//    private ArrayList<ArrayList<CellInfo>> copyView(ArrayList<ArrayList<CellInfo>> view) {
//        // 
//        return new ArrayList<ArrayList<CellInfo>>();
//    }

	/**
	 * Prüft ob der Hunter gewünschte Position erreicht hat.
	 */
	public boolean isZiel(Point zielPositon) {

		if (this.hunterPosition.equals(zielPositon)) { // Der Hunter hat die gewünschte Postion erreicht.
			return true;
		}
		return false;
	}

	/**
	 * Berechne aus dem:
	 * 
	 * view, 
	 * HunterPosition, 
	 * und HunterDirection  
	 * 
	 * einen Hashcode
	 * 
	 * @return
	 */
	@Override
	public int hashCode() {

		String hashString = new String(sView);

		if (this.hunterPosition != null) {
			hashString += hunterPosition.toString();
		}

		if (this.hunterDirection != null) {
			// PS: Damit Zwei Knoten mit gleiche HunterPosition, sich bei der
			// HunterDirection unterscheiden können.
			// Um Loops natürlich zu vermeinden.
			hashString += this.hunterDirection.toString();
		}

		return hashString.hashCode();
	}

	/** Berechne die neue Positions der Hunter auf Basis der Bewegungsrichtung(
	* HunterDirection + HunterAction )
	* 
	* oder 
	* 
	* Aktualisiert die HunterDirection auf Basis der HunterDirection + HunterAction.
	* 
	*/
	
	public Point berechneNeuePosition(Point vorgaengerPos, Direction vorgaengerDirection, HunterAction action) {
		Point nachfolgerPos = new Point(vorgaengerPos.getX(), vorgaengerPos.getY());

		switch (action) {
		case TURN_LEFT: {
			switch (vorgaengerDirection) {
			case NORTH:
				this.hunterDirection = Direction.WEST;
				break;
			case EAST:
				this.hunterDirection = Direction.NORTH;
				break;
			case SOUTH:
				this.hunterDirection = Direction.EAST;
				break;
			case WEST:
				this.hunterDirection = Direction.SOUTH;
				break;
			default:
				throw new IllegalArgumentException("Unzulässige HunterAction");
			}
			break;
		}
		case TURN_RIGHT: {
			switch (vorgaengerDirection) {
			case NORTH:
				this.hunterDirection = Direction.EAST;
				break;
			case EAST:
				this.hunterDirection = Direction.SOUTH;
				break;
			case SOUTH:
				this.hunterDirection = Direction.WEST;
				break;
			case WEST:
				this.hunterDirection = Direction.NORTH;
				break;
			default:
				throw new IllegalArgumentException("Unzulässige HunterAction");
			}
			break;
		}
		case GO_FORWARD: {
			switch (vorgaengerDirection) {
			case NORTH:
				nachfolgerPos.setY(nachfolgerPos.getY() - 1);
				break;
			case EAST:
				nachfolgerPos.setX(nachfolgerPos.getX() + 1);
				break;
			case SOUTH:
				nachfolgerPos.setY(nachfolgerPos.getY() + 1);
				break;
			case WEST:
				nachfolgerPos.setX(nachfolgerPos.getX() - 1);
				break;
			default:
				throw new IllegalArgumentException("Unzulässige HunterAction");
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unzulässige HunterAction");
		}

		return nachfolgerPos;
	}

	/**
	 * Berechne die notwendigen actions um diesen Knoten vom Start aus zu erreichen
	 */
	public LinkedList<HunterAction> berechneHunterActions() {
		Knoten vorgaenger = this.vorgaenger;
		Knoten aktueller = this;
		LinkedList<HunterAction> hunterActionList = new LinkedList<>();

		while (vorgaenger != null) {
			if (vorgaenger.hunterPosition.equals(aktueller.hunterPosition)) {
				HunterAction action = this.getTurnDirection(aktueller.getHunterDirection(), vorgaenger.getHunterDirection());
				if( action != null) {
					hunterActionList.add(0, action);
				}
			} else {
				hunterActionList.add(0, HunterAction.GO_FORWARD);
			}
			
			aktueller = vorgaenger;
			vorgaenger = vorgaenger.vorgaenger;
		}
		return hunterActionList;
	}

	/**
	 * Auf Basis von
	 * 
	 * @param currentDirection,
	 * und @param previousDirection
	 * 
	 * berechnet die Drehungsrichtung der Hunter, also entweder TURN_LEFT oder TURN_RIGHT
	 */
	private HunterAction getTurnDirection(Direction currentDirection, Direction previousDirection) {
		HunterAction action = null;
		switch (previousDirection) {
		case NORTH: {
			switch (currentDirection) {
			case NORTH:
				// Diese Bewegung ist nicht möglich
				throw new IllegalArgumentException("getTurnDirection: Unzulässige HunterAction");
			case EAST:
				action = HunterAction.TURN_RIGHT;
				break;
			case SOUTH:
				// Diese Bewegung ist nicht möglich
				throw new IllegalArgumentException("getTurnDirection: Unzulässige HunterAction");
			case WEST:
				action = HunterAction.TURN_LEFT;
				break;
			}
		}
			break;
		case EAST: {
			switch (currentDirection) {
			case NORTH:
				action = HunterAction.TURN_LEFT;
				break;
			case EAST:
				// Diese Bewegung ist nicht möglich
				throw new IllegalArgumentException("getTurnDirection: Unzulässige HunterAction");
			case SOUTH:
				action = HunterAction.TURN_RIGHT;
				break;
			case WEST:
				// Diese Bewegung ist nicht möglich
				throw new IllegalArgumentException("getTurnDirection: Unzulässige HunterAction");
			}
		}
			break;
		case SOUTH: {
			switch (currentDirection) {
			case NORTH:
				// Diese Bewegung ist nicht möglich
				throw new IllegalArgumentException("getTurnDirection: Unzulässige HunterAction");
			case EAST:
				action = HunterAction.TURN_LEFT;
				break;
			case SOUTH:
				// Diese Bewegung ist nicht möglich
				throw new IllegalArgumentException("getTurnDirection: Unzulässige HunterAction");
			case WEST:
				action = HunterAction.TURN_RIGHT;
				break;
			}
		}
			break;
		case WEST: {
			switch (currentDirection) {
			case NORTH:
				action = HunterAction.TURN_RIGHT;
				break;
			case EAST:
				// Diese Bewegung ist nicht möglich
				throw new IllegalArgumentException("getTurnDirection: Unzulässige HunterAction");
			case SOUTH:
				action = HunterAction.TURN_LEFT;
				break;
			case WEST:
				// Diese Bewegung ist nicht möglich
				throw new IllegalArgumentException("getTurnDirection: Unzulässige HunterAction");
			}
		}
			break;
		}
		return action;
	}
	
	public float getPfadkosten() {
		return pfadkosten;
	}

	public void setPfadkosten(float pfadkosten) {
		this.pfadkosten = pfadkosten;
	}

	public Knoten getVorgaenger() {
		return vorgaenger;
	}

	public void setSchaetzwert(float schaetzwert) {
		this.schaetzwert = schaetzwert;
	}

	public float getSchaetzwert() {
		return this.schaetzwert;
	}

	public float getBewertung() {
		return pfadkosten + schaetzwert;
	}

	public ArrayList<ArrayList<CellInfo>> getView() {
		return this.view;
	}
	
	public Point getPos() {
		return hunterPosition;
	}

	public Direction getHunterDirection() {
		return this.hunterDirection;
	}
	
	public Point getHunterPosition() {
		return hunterPosition;
	}
	
	@Override
	public String toString() {
		String out = "";
		out += "Knoten\n{ ";
		out += "HunterPosition: " + this.hunterPosition + " HunterDirection : " + this.hunterDirection + " Pfadkosten: "
				+ this.pfadkosten + " Schaetzwert: " + this.schaetzwert + " }";
		return out;
	}

}
