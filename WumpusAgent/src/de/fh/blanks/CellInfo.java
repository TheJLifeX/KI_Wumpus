package de.fh.blanks;

public class CellInfo
{
	private CellType type;
    private double probability;

    public CellInfo(CellType type, double probability)
    {
    	 this.type = type;
        this.probability = probability; 
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

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	
    @Override
    public String toString() {
    	return "(" + "T:" + type.name().substring(0, 1) + " P:" + this.probability + ")";
    }
    
}
