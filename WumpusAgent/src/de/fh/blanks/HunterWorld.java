package de.fh.blanks;

import java.util.ArrayList;

public class HunterWorld<Typ>
{
    private ArrayList<ArrayList<Typ>> world;

    public HunterWorld()
    {
        world = new ArrayList<ArrayList<Typ>>();
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
			this.add(x, y, newCellInfo);
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
}
