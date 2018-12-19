package de.fh.blanks;

public class CellInfo
{
	private CellType type;
    private double probabilityWumpus;
    private double probabilityPit;

    public CellInfo(CellType type, double probabilityWumpus, double probabilityPit)
    {
    	this.type = type;
    
        this.probabilityWumpus = probabilityWumpus; 
        this.probabilityPit = probabilityPit;
    }
    
    public double getProbabilityWumpus() {
		return probabilityWumpus;
	}

	public void setProbabilityWumpus(double probabilityWumpus) {
		this.probabilityWumpus = probabilityWumpus;
	}

	public double getProbabilityPit() {
		return probabilityPit;
	}

	public void setProbabilityPit(double probabilityPit) {
		this.probabilityPit = probabilityPit;
	}

	public CellInfo(CellType type) {
    	this.type = type;
    }

	public CellType getType() {
		return type;
	}

	public void setType(CellType type) {
		this.type = type;
	}

	
    @Override
    public String toString() {
    	if( this.type == CellType.WALL) {
    		return "(" + type.name().substring(0, 2) + ")";
    	} else {
    		return "(" + type.name().substring(0, 2) + " Pp:" + this.getProbabilityPit() + " Pw:" + this.getProbabilityWumpus() + ")";
    	}
    }
    
}
