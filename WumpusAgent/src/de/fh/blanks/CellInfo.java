package de.fh.blanks;

import java.util.Comparator;
import java.util.LinkedList;

public class CellInfo
{
	private CellType type;
	private Point position;
    private double probabilityPit;

    /**
     * Hier sind alle noch unbekannte Zelle gespeichert, die rund um den bekannnten Welt liegen.
     */
	private static LinkedList<CellInfo> unknownCells = new LinkedList<>();

    public CellInfo(CellType type){
    	this.type = type;
    }
    
    /**
     * Konstrucktor f�r CellInfo von Typ WALL.
     * 
     * Hier wird auch alle CellInfo von Typ WALL in einer unknownCells gespeichert.
     * Die Koordinaten x und y werden genutzt um ein "Wall" Zelle als target zu sezten, und dorthin gehen.
     */
	public CellInfo(int x, int y, double probabilityPit) {
		this.type = CellType.UNKWON;
		this.position = new Point(x, y);
		this.probabilityPit = probabilityPit;
		CellInfo.unknownCells.add(0, this);
	}

	public Double getEstimate() {
		return this.probabilityPit;
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

	public CellType getType() {
		return type;
	}

	public void setType(CellType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		if (this.type != CellType.UNKWON) {
			return "(" + type.name() + ")";
		} else {
			return "(" + type.name().substring(0, 2) + " pit:" + this.probabilityPit + ")";
		}
	}
    
	public static LinkedList<CellInfo> getUnknownCells(){
		return CellInfo.unknownCells;
	}
	
	public static void sortUnkwonCells() {
		CellInfo.unknownCells.sort(new Comparator<CellInfo>() {
			@Override
			public int compare(CellInfo c1, CellInfo c2) {
				return Double.compare(c1.getEstimate(), c2.getEstimate());
			}
		});
	}
}
