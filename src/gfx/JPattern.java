package gfx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;

import javax.swing.JPanel;

import data.Level;
import parts.Pattern;
import parts.Wall;

@SuppressWarnings("serial")
public class JPattern extends JPanel {
	
	//in case I need level colors?
	//private Level level;
	private Pattern pattern;
	private int selectedIndex = -1;
	
	public JPattern(Level level, Pattern pattern) {
		super();
		setPreferredSize(new Dimension(640,640));
        this.pattern = pattern;
    }
	
	public void paintWall(Graphics2D g, Wall wall, boolean hilight) {
		
		Polygon p = new Polygon();
		int centerx = getSize().width / 2;
		int centery = getSize().height / 2;
		int side = wall.getSide();
		int height = wall.getHeight();
		int dist = wall.getDistance();
		double radians = ((double)side / (double)pattern.getSides()) * (Math.PI * 2);
		double radians2 = (((double)side + 1) / (double)pattern.getSides()) * (Math.PI * 2);
		p.addPoint((int)(Math.cos(radians) * (dist + height)) + centerx, 
				   y((int)(Math.sin(radians) * (dist + height))) + centery);
		p.addPoint((int)(Math.cos(radians2) * (dist + height)) + centerx, 
				   y((int)(Math.sin(radians2) * (dist + height))) + centery);
		p.addPoint((int)(Math.cos(radians2) * dist) + centerx, 
				   y((int)(Math.sin(radians2) * dist)) + centery);
		p.addPoint((int)(Math.cos(radians) * dist) + centerx, 
				   y((int)(Math.sin(radians) * dist)) + centery);
		g.setColor(Color.RED);
		g.fillPolygon(p);
		if(hilight) {
			Stroke s = g.getStroke();
			g.setStroke(new BasicStroke(2));
			g.setColor(Color.BLACK);
			g.drawPolygon(p);
			g.setStroke(s);
		}
	}

    private int y(int i) {
		return i * -1;
	}

	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getSize().width, getSize().height);
        g2.setColor(Color.RED);
        for(Wall wall : pattern.getWalls()) paintWall(g2, wall, false);
        if(selectedIndex >= 0 && selectedIndex < pattern.getWalls().length) {
        	paintWall(g2, pattern.getWalls()[selectedIndex], true);
        }
    }

	public void selectIndex(int index) {
		selectedIndex = index;
	}
}
