package de.fh.blanks;

import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import de.fh.wumpus.enums.HunterActionEffect;
import java.util.ArrayList;
import java.util.Hashtable;

public class HunterWorld {
	private ArrayList<ArrayList<CellInfo>> view;

//	private HunterPercept previousPercept; Diese Variable wird eventuell später benutzt werden.
	private HunterAction nextAction;
//	private HunterActionEffect previousActionEffect; Diese Variable wird eventuell später benutzt werden.
	private HunterPercept percept;
	private Hashtable<Integer, Integer> stenchRadar;

	private Point hunterPosition = new Point(1, 1);
	private Direction hunterDirection = Direction.EAST;
	private int numArrows = 5;
	private Point goldPosition = new Point(-1, -1);
	private boolean hasGold = false;
	private boolean wumpusAlive = true;

	public HunterWorld() {
		view = new ArrayList<ArrayList<CellInfo>>();
	}

	/**
	 * Wichtig zu verstehen: Die updateState Methode wird zuerst ausgeführt, dann die action Methode.
	 * 
	 * @param percept: Information, die wir von der Server bekommen haben, durch unsere letzte Action.		 
	 * @param actionEffect: Information, die wir von der Server bekommen haben, durch unsere letzte Action.
	 */
	public void updateState(HunterPercept percept, HunterActionEffect actionEffect) {
		/**
		 * Je nach Sichtbarkeit & Schwierigkeitsgrad (laut Serverkonfiguration) aktuelle
		 * Wahrnehmung des Hunters. Beim Wumpus erhalten Sie je nach Level mehr oder
		 * weniger Mapinformationen.
		 */
		this.percept = percept;

		// Aktuelle Reaktion des Server auf die letzte Ã¼bermittelte Action.

		// Alle möglichen Serverrückmeldungen:
		if (actionEffect == HunterActionEffect.GAME_INITIALIZED) {
			// Erster Aufruf
			this.updateCell(new CellInfo(CellType.EMPTY));
		}

		if (actionEffect == HunterActionEffect.GAME_OVER) {
			// Das Spiel ist verloren
			this.nextAction = HunterAction.QUIT_GAME;
		}

		if (actionEffect == HunterActionEffect.BUMPED_INTO_WALL) {
			this.updateCell(null);
			// TODO nextAction des Hunter anpassen.                
		}

		if (actionEffect == HunterActionEffect.BUMPED_INTO_HUNTER) {
			// Nur bei Multiplayermodus
			// Letzte Bewegungsaktion war ein Zusammenstoß einem weiteren Hunter
		}

		if (actionEffect == HunterActionEffect.GOLD_FOUND) {
			// Nach HunterAction.GRAB wurde das Gold gefunden.
			this.updateCell(new CellInfo(CellType.EMPTY));
			this.goldPosition.set(-1, -1);
		}

		if (actionEffect == HunterActionEffect.WUMPUS_KILLED) {
			// TODO Wumpus postion in der Welt aktualisieren, ähnlich wie oben

		}

		if (actionEffect == HunterActionEffect.NO_MORE_SHOOTS) {
			// TODO: Percept hat hierfür keinen Wert
			// TODO A* bis zur Position (1, 1)
		}

//		this.previousActionEffect = actionEffect;

		/*
		 * Mögliche Percepts Über die Welt erhält der Wumpushunter:
		 * 
		 * percept.isBreeze(); percept.isBump(); percept.isGlitter();
		 * percept.isRumble(); percept.isScream(); percept.isStench();
		 * percept.getWumpusStenchRadar()
		 */

		if (actionEffect == HunterActionEffect.MOVEMENT_SUCCESSFUL) {
			// Letzte Bewegungsaktion war gültig
			if (percept.isBreeze()) {
				this.updateCell(new CellInfo(CellType.BREEZE));
			} else if (percept.isStench()) {
				this.updateCell(new CellInfo(CellType.STENCH));
			} else {
				this.updateCell(new CellInfo(CellType.EMPTY));
			}
		}

		/*
		 * percept.getWumpusStenchRadar() enthält alle Wumpi in max. R(ie)eichweite in
		 * einer Hashtable. Jeder Wumpi besitzt eine unique WumpusID (getKey). Die
		 * Manhattendistanz zum jeweiligen Wumpi ergibt sich aus der Gestanksitensität
		 * (getValue).
		 */

		// Beispiel:
		stenchRadar = this.percept.getWumpusStenchRadar();

		// Gebe alle riechbaren Wumpis aus
//		System.out.println("WumpusID: Intensitaet");
		if (stenchRadar.isEmpty()) {
//			System.out.println("Kein Wumpi zu riechen");
		}
//		for (Map.Entry<Integer, Integer> g : stenchRadar.entrySet()) {
//			System.out.println(g.getKey() + ":\t\t" + g.getValue());
//		}
	}

	/**Hier wird nur:
	 * Wenn action == TURN_LEFT oder TURN_RIGHT, die hunterDirection aktualisiert.
	 * Wenn action == GO_FORWARD, die hunterPosition aktualisiert.
	 * 
	 * Hier wird nicht bestimmt welche Aktion als nächstes ausgeführt wird,
	 * das wird in der updateState Methode gemacht.
	 * 
	 * Auf Basis von der Informationen, die durch unsere letzte Aktion von Server gesendet wurden,
	 * bestimmen wir die nächste Aktion.
	 * 
	 * @param action : entpricht letzte Action, die auf dem Server ausgeführt wurde.
	 */
	public void processAction(HunterAction action) {
		switch (action) {
		case TURN_LEFT: {
			switch (hunterDirection) {
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
			switch (hunterDirection) {
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
			switch (this.hunterDirection) {
			case NORTH:
				this.hunterPosition.setY(this.hunterPosition.getY() - 1);
				break;
			case EAST:
				this.hunterPosition.setX(this.hunterPosition.getX() + 1);
				break;
			case SOUTH:
				this.hunterPosition.setY(this.hunterPosition.getY() + 1);
				break;
			case WEST:
				this.hunterPosition.setX(this.hunterPosition.getX() - 1);
				break;
			default:
				throw new IllegalArgumentException("Unzulässige HunterAction");
			}
			break;
		}
		default:
			// Hier soll nichts gemacht werden
			break;
		}
		action = this.nextAction;
	}

	/**
	 * Gibt das Element von der gewünschte Postion, sonst null wenn das Element nicht existiert.
	 */
	public CellInfo get(int x, int y) {
		if (x < 0 || y < 0) {
			return null;
		}

		try {
			ArrayList<CellInfo> row = this.view.get(y);
			CellInfo cellInfo = row.get(x);
			return cellInfo;

		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}


	/**
	 * Fügt ein Element ein Element an der gewünschte Postion ein, oder aktualisiert ihn, wenn er schon
	 * vorhanden ist
	 *  
	 * @return das vorherige Element, wenn ein Element aktualisiert wurde oder null falls kein Element aktualisiert wurde. 
	 */
	public CellInfo set(int x, int y, CellInfo newCellInfo) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("SET: Unzulässige Koordinaten " + "(" + x + ", " + y + ")" + "\n"
					+ "x und y Koodirnaten müssen größer gleich 0 sein.");
		}

		try {
			ArrayList<CellInfo> row = this.view.get(y);
			CellInfo previousCellInfo = row.set(x, newCellInfo);
			return previousCellInfo;

		} catch (IndexOutOfBoundsException e) {
			this.add(x, y, newCellInfo);
			return null;
		}
	}

	/**
	 * Fügt ein Element an der gewünschte Postion ein
	 * 
	 * Wird nur in der Set Methode genutzt.
	 */
	private boolean add(int x, int y, CellInfo newCellInfo) {
		if (x < 0 || y < 0) {
			return false;
		}

		try {
			if (y >= this.view.size()) {

				ArrayList<CellInfo> newRow = null;
				for (int j = this.view.size(); j <= y; j++) {
					newRow = new ArrayList<CellInfo>();
					this.view.add(newRow);
					for (int k = 0; k < x; k++) {
						newRow.add(null);
					}
				}
				newRow.add(newCellInfo);
			} else {

				ArrayList<CellInfo> row = this.view.get(y);
				if (x >= row.size()) {
					for (int i = row.size(); i < x; i++) {
						row.add(null);
					}
					row.add(newCellInfo);
				} else {
					this.set(x, y, newCellInfo);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String s = "";
		for (int row = 0; row < this.view.size(); row++) {
			for (int col = 0; col < this.view.get(row).size(); col++) {
				final CellInfo cellInfo = this.get(col, row);
				if (cellInfo != null) {
					s += cellInfo.getType().toString().substring(0, 2);
				} else {
					s += "NULL";
				}
			}
		}
		return s;
	}

	/**
	 * Gibt alle Information von der HunterWorl aus der Konsole aus.
	 */
	public void print() {
		String out = "HUNTER_WORLD\n{ hunterPosition: " + hunterPosition + ", hunterDirection: " + hunterDirection
				+ ", goldPosition: " + goldPosition + ", hasGold: " + hasGold + ", numArrows: " + numArrows
				+ ", wumpusAlive: " + wumpusAlive + " }\n";

		int rows = view.size();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < view.get(row).size(); col++) {
				CellInfo cellInfo = get(col, row);
				if (cellInfo != null)
					out += cellInfo.toString() + "   ";
				else
					out += "NULL ";
			}
			out += "\n";
		}

		System.out.println(out);
	}

	public Direction getHunterDirection() {
		return hunterDirection;
	}

	/**
	 * Aktulisiert eine Zelle, an der Stelle, wo der Hunter liegt, auf Basis von der Information,
	 * die vom Server bekommen wurden.
	 * 
	 * @param cellInfo
	 */
	public void updateCell(CellInfo cellInfo) {
		this.set(this.hunterPosition.getX(), this.hunterPosition.getY(), cellInfo);
	}
	
	public ArrayList<ArrayList<CellInfo>> getView(){
		return this.view;
	}
	
	public Point getHunterPosition() {
		return this.hunterPosition;
	}
}
