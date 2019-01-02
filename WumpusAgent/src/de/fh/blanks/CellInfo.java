package de.fh.blanks;

import java.util.Comparator;
import java.util.LinkedList;

public class CellInfo
{
	private CellType type;
	private Point position;
    private double probabilityPit;
    private double probabilityWumpus;
    
    /**
     * Hier sind alle noch unbekannte Zelle gespeichert, die rund um die bekannte Welt liegen.
     */
	private static LinkedList<CellInfo> wallList = new LinkedList<>();

    public CellInfo(CellType type){
    	this.type = type;
    }
    
    /**
     * Konstrucktor für CellInfo von Typ WALL.
     * 
     * Hier wird auch alle CellInfo von Typ WALL in einer wallList gespeichert.
     * Die Koordinaten x und y werden genutzt um ein "Wall" Zelle als target zu sezten, und dorthin gehen.
     */
	public CellInfo(int x, int y, double probabilityPit, double probabilityWumpus) 
	{
		type = CellType.WALL;
		position = new Point(x, y);
		probabilityPit = probabilityPit;
		probabilityWumpus = probabilityWumpus;
		CellInfo.wallList.add(this);
	}

	public Double getEstimate() {
		return this.probabilityPit + this.probabilityWumpus;
	}
	
	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public double getProbabilityPit() {
		return probabilityPit;
	}

	public void setProbabilityPit(double probabilityPit) {
		this.probabilityPit = probabilityPit;
	}

	public double getProbabilityWumpus() {
		return probabilityWumpus;
	}

	public void setProbabilityWumpus(double probabilityWumpus) {
		this.probabilityWumpus = probabilityWumpus;
	}
	
	public CellType getType() {
		return type;
	}

	public void setType(CellType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		if (this.type != CellType.WALL) {
			return "(" + type.name() + ")";
		} else {
			return "(" + type.name().substring(0, 2) + " pit:" + this.probabilityPit + " wum:" + this.probabilityWumpus
					+ ")";
		}
	}
    
	public static LinkedList<CellInfo> getWallList(){
		return CellInfo.wallList;
	}
	
	public static void sortWallList() {
		CellInfo.wallList.sort(new Comparator<CellInfo>() {
			@Override
			public int compare(CellInfo c1, CellInfo c2) {
				return Double.compare(c1.getEstimate(), c2.getEstimate());
			}
		});
	}
}
