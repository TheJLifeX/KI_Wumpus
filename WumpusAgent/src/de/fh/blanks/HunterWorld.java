package de.fh.blanks;

import de.fh.wumpus.HunterPercept;
import de.fh.wumpus.enums.HunterAction;
import javafx.util.Pair;

import java.util.ArrayList;

public class HunterWorld<Typ>
{
	/**
	 * 2D-Array with locatable world information. Other information that has no location (e.g. number of arrows)
	 * can not be reasonably stored here.
	 */
    private ArrayList<ArrayList<Typ>> world;

    /**
     * Previous HunterPercept that was given to this HunterWorld.
     */
    private HunterPercept previousPercept = null;

    /**
     * Previous HunterAction that was given to this HunterWorld.
     */
    private HunterAction previousAction = null;

	/**
	 * Current HunterPosition. Always 1, 1 at the start.
	 */
	private Point hunterPosition = new Point(1, 1);

    /**
     * Current line-of-sight of Hunter. Default is always East(siehe Aufgabenstellung).
     */
    private Direction hunterDirection = Direction.EAST;

    /**
     * Stores number of Arrows the Hunter currently has.
     * // TODO: Im Konstruktor übergeben, falls möglich.
     */
    private int numArrows = 5;

	/**
	 * Gold Position. Always unknown at the start.
	 */
	private Point goldPosition = new Point(-1, -1);

	/**
	 * Stores if Hunter has gold.
	 */
	private boolean hasGold = false;

	/**
	 * Stores if Wumpus is alive (only applicable to world with one wumpus)
	 * // TODO: Multiple Wumpi
	 */
	private boolean wumpusAlive = true;

    public HunterWorld()
    {
        world = new ArrayList<ArrayList<Typ>>();
    }

	/**
	 * Returns current line-of-sight of Hunter.
	 * @return Aktuelle Blickrichtung des Hunters.
	 */
	public Direction getHunterDirection()
	{
		return hunterDirection;
	}

	/**
	 * Processes given HunterPercept and updates this HunterWorld accordingly.
	 * @param percept Given HunterPercept.
	 */
	public void processPercept(HunterPercept percept)
	{
		if (percept.isBump())
		{
		}

		if (percept.isBreeze())
		{

		}
		if (percept.isGlitter())
		{

		}
		if (percept.isRumble())
		{

		}
		if (percept.isScream())
		{

		}
		if (percept.isStench())
		{

		}
		previousPercept = percept;
	}

	/**
	 * Processes given HunterAction and updates this HunterWorld accordingly.
	 * @param action Given HunterAction.
	 * @implNote Das wird ein riesiger switch. Andere Möglichkeit Polymorphie ? Vorschläge erwünscht!
	 */
	public void processAction(HunterAction action)
	{
		switch (action)
		{
			case TURN_LEFT:
			{
				switch(hunterDirection)
				{
					case NORTH:
						hunterDirection = Direction.WEST;
						break;
					case EAST:
						hunterDirection = Direction.NORTH;
						break;
					case SOUTH:
						hunterDirection = Direction.EAST;
						break;
					case WEST:
						hunterDirection = Direction.SOUTH;
						break;
				}
				break;
			}
			case TURN_RIGHT:
			{
				switch(hunterDirection)
				{
					case NORTH:
						hunterDirection = Direction.EAST;
						break;
					case EAST:
						hunterDirection = Direction.SOUTH;
						break;
					case SOUTH:
						hunterDirection = Direction.WEST;
						break;
					case WEST:
						hunterDirection = Direction.NORTH;
						break;
				}
				break;
			}
			case GRAB:
			{
				// TODO: if HunterPosiion == GoldPosition => hasGold = true
				if (hunterPosition.equals(goldPosition))
					hasGold = true;
				break;
			}
			case SIT:
			{
				// TODO: Keine Ahnung, was man hier machen kann.
				break;
			}
			case SHOOT:
			{
				// TODO: if scream => numWumpi--, numArrows--
				if (numArrows != 0)
					numArrows--;
				break;
			}
			case GO_FORWARD:
			{
			    // TODO: Darf ich hier einfach returnen ? Ja, oder ? Problem: Inkrementiert einen zu viel, weil Bump durch forward erkannt wird.
			    if (previousPercept.isBump())
                    return;

				switch(hunterDirection)
				{
					case NORTH:
						hunterPosition.setY(hunterPosition.getY() - 1);
						break;
					case EAST:
						hunterPosition.setX(hunterPosition.getX() + 1);
						break;
					case SOUTH:
						hunterPosition.setY(hunterPosition.getY() + 1);
						break;
					case WEST:
						hunterPosition.setX(hunterPosition.getX() - 1);
						break;
				}
				break;
			}
			case QUIT_GAME:
			{
				// TODO: Keine Ahnung, was man hier machen kann.
				break;
			}
		}
		previousAction = action;
	}

    public Typ get(int x, int y) {
    	if( x < 0 ||  y < 0) {
    		return null;
    	}
    	
    	try {
    		ArrayList<Typ> row = this.world.get(y);
    		Typ cellInfo = row.get(x);
    		return cellInfo;
			
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
    }
    
    public Typ set(int x, int y, Typ newCellInfo) {
    	if( x < 0 ||  y < 0) {
    		return null;
    	}
    	
    	try {
    		ArrayList<Typ> row = this.world.get(y);
    		Typ previousCellInfo = row.set(x, newCellInfo);
    		return previousCellInfo;
			
		} catch (IndexOutOfBoundsException e) {
			this.add(x, y, newCellInfo); // Warum wird hinzugefügt, wenn Exception ?
			return null;
		}
    }
    
    public boolean add(int x, int y, Typ newCellInfo) {
    	if( x < 0 ||  y < 0) {
    		return false;
    	}
    	
    	try {
    		if( y >= this.world.size()) {
    			
    			ArrayList<Typ> newRow = null;
    			for(int j = this.world.size(); j <= y; j++) {
    				newRow = new ArrayList<Typ>();
        			this.world.add(newRow);
        			for(int k = 0; k < x; k++) {
        				newRow.add(null);
        			}
    			}
    			newRow.add(newCellInfo);
    		} else {
    			
    			ArrayList<Typ> row = this.world.get(y);
    			if( x >= row.size()) {  				
        			for(int i = row.size(); i < x; i++) {
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
    
    public void print() {
    	String out = "";
    	for(int y = 0 ; y < this.world.size(); y++) {
    		for(int x = 0; x < this.world.get(y).size(); x++) {
    			final Typ cellInfo = this.get(x, y);
    			if( cellInfo != null) {
    				out += this.get(x, y).toString() + "  ";
    			} else {
    				out += "NULL         ";
    			}
    		}
    		out += "\n";
    	}
    	System.out.println(out);
    }

    @Override
	public String toString()
	{
	    String s = "HUNTER_WORLD\n{ hunterPosition: " + hunterPosition + ", hunterDirection: " + hunterDirection + ", goldPosition: " + goldPosition + ", hasGold: " + hasGold
                + ", numArrows: " + numArrows + ", wumpusAlive: " + wumpusAlive + " }\n";

	    int rows = world.size();
	    for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < world.get(row).size(); col++)
            {
                Typ cellInfo = get(row, col);
                if (cellInfo != null)
                    s += get(row, col).toString() + "  ";
                else
                    s += "NULL         ";
            }
            s += "\n";
        }

		return s.toString();
	}
}
