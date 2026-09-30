package edu.ncsu.csc411.ps02.environment;

/**
 * A simple object representing the Tiles in the
 * environment.
 * DO NOT MODIFY.
 * @author Adam Gaweda
 */
public class Tile {
	private TileStatus status;
	
	private boolean inPath;
	
	
	
	public Tile(TileStatus status) {
		this.status = status;
		setInPath(false);
		
	}
	
	public boolean isInPath() {
		return inPath;
	}
	
	public void setInPath(boolean inPath) {
		this.inPath = inPath;	
    }
	
	
	
	public TileStatus getStatus() { return status; }
	public String toString() { return ""+status.toString().charAt(0); }
}
