package de.fh.blanks;

public class CellInfo
{
	private CellTyp type;
    private double probability;

    public CellInfo(CellTyp type, double probability)
    {
    	 this.type = type;
        this.probability = probability; 
    }
    
    public CellInfo(CellTyp type) {
    	this.type = type;
    }

	public CellTyp getType() {
		return type;
	}

	public void setType(CellTyp type) {
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
