package parts;

public class Wall {
	public static final int BYTE_LENGTH = 3 * 2; //3 chars consisting of 2 bytes each
	public static final int MIN_DISTANCE = 80;
	private int distance;
	private int height;
	private int side;
	
	public enum Set {
		DISTANCE,
		HEIGHT,
		SIDE
	}
	
	public Wall() {
		this.distance = 0;
		this.height = 16;
		this.side = 0;
	}
	
	public Wall(int distance, int height, int side) {
		this.distance = distance - MIN_DISTANCE;
		this.height = height;
		this.side = side;
	}
	
	public char getDistance() {
		return (char)(distance + MIN_DISTANCE);
	}
	
	public char getHeight() {
		return (char)height;
	}
	
	public char getSide() {
		return (char)side;
	}
	
	public void setDistance(int dist) {
		this.distance = dist;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setSide(int side) {
		this.side = side;
	}
	
	public String toString() {
		return "[dist=" + (int)distance + ",height=" + (int)height + ",side=" + (int)side + "]";
	}
}
