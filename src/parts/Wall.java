package parts;

import org.w3c.dom.Element;

import red.Dynamic;
import red.UtilXML;

public class Wall {
	
	//Globals
	public static final int MIN_DISTANCE = 80;
	
	//XML
	public static final String XML_HEADER= "Wall";
	public static final String XML_DISTANCE = "Distance";
	public static final String XML_HEIGHT = "Height";
	public static final String XML_SIDE = "Side";
	
	public Wall() {
		setDefaults();
	}
	
	public Wall(Element e) {
		setDefaults();
		try {
			readXML(e);
		} catch(Exception ex) {
			System.out.println("Failure to parse wall! Using default wall information.");
		}
	}
	
	//READ AND WRITE VARIABLES
	private int distance;
	private int height;
	private int side;

	private void setDefaults() {
		this.distance 	= 0;
		this.height 	= 16;
		this.side 		= 0;
	}
	
	public void readXML(Element e) throws Exception {
		distance 	= UtilXML.getInt(e, XML_DISTANCE);
		height 		= UtilXML.getInt(e, XML_HEIGHT);
		side 		= UtilXML.getInt(e, XML_SIDE);
	}
	
	public void writeXML(Element e) {
		UtilXML.putInt(e, XML_DISTANCE, distance);
		UtilXML.putInt(e, XML_HEIGHT, height);
		UtilXML.putInt(e, XML_SIDE, side);
	}
	
	public void writeBIN(Dynamic d) {
		d.putChar(getDistance());
		d.putChar(getHeight());
		d.putChar(getSide());
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
