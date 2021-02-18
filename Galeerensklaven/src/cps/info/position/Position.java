package cps.info.position;

public class Position implements PositionI{
	private int x, y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
		
	}
	
	public int getX() {return this.x;}
	public int getY() {return this.y;}
	@Override
	public double distance(PositionI other) {
		return (Math.sqrt((this.x - other.getX())*(this.x - other.getX())+(this.y - other.getY())*(this.y - other.getY())));
	}
	

}
