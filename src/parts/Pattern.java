package parts;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import data.Level;
import data.Project;
import gfx.JPattern;
import red.Dynamic;
import red.ListData;
import red.Util;
import red.UtilXML;

public class Pattern {
	
	//Globals
	public static final String DIR_NAME = "patterns";
	public static final String XML_HEADER = "Pattern";
	public static final String BIN_HEADER = "PTN1.1";
	public static final String BIN_FOOTER = "ENDPTN";
	
	//Finals
	public final Project project;
	public final File file;
	public final JFrame frame;
	
	public Pattern(Project project, String name) {
		this.frame = new JFrame("New pattern '" + name + "'");
		this.project = project;
		this.file = new File(project.dir, DIR_NAME + File.separator + name + ".xml");
		setDefaults();
	}
	
	public Pattern(Project project, File patternFile) {
		this.frame = new JFrame("Pattern '" + patternFile.getName() + "'");
		this.project = project;
		this.file = patternFile;
		setDefaults();
		try {
			Document doc = UtilXML.openXML(patternFile, XML_HEADER);
			readXML(doc.getDocumentElement());
		} catch(Exception ex) {
			System.out.println("Failure to parse pattern file " + toString() + "! Using default pattern information.");
		}
	}
	
	//READ AND WRITE VARIABLES
	public static final String XML_SIDES_REQUIRED = "SidesRequired";
	private int sidesRequired;
	private List<Wall> walls;
	
	private void setDefaults() {
		this.sidesRequired 	= 6;
		this.walls 			= new ArrayList<>();
	}
	
	public void readXML(Element e) throws Exception {
		this.sidesRequired 	= UtilXML.getInt(e, XML_SIDES_REQUIRED);
		this.walls 			= UtilXML.getWalls(e);
	}
	
	public void writeXML(Element e) {
		UtilXML.putInt(e, XML_SIDES_REQUIRED, sidesRequired);
		UtilXML.putWalls(e, walls);
	}
	
	public void writeBIN(Dynamic d) throws IOException {
		d.putRawString(BIN_HEADER);
		d.putInt(sidesRequired);
		d.putInt(walls.size());
		for(Wall wall : walls) wall.writeBIN(d);
		d.putRawString(BIN_FOOTER);
	}
	
	public void edit(Level level) {
		frame.setLayout(new BorderLayout());
		frame.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {};
			public void windowClosing(WindowEvent e) {
				level.refreshPatterns();
				level.frame.setVisible(true);
			}
		});
		
		JPanel leftConfig = Util.startFrame(new GridLayout(0,1));
		
		JPanel wallList = Util.startFrame(new BorderLayout());
		ListData jWALL = Util.addTitledListToPanel(wallList, BorderLayout.CENTER, "Walls", walls);
		
		JPanel wallListButtons = Util.startFrame(new GridLayout(1,0));
		JButton jADDW = Util.addButtonToPanel(wallListButtons, BorderLayout.SOUTH, "Add Wall");
		JButton jREMO = Util.addButtonToPanel(wallListButtons, BorderLayout.NORTH, "Remove Wall");
		wallList.add(wallListButtons, BorderLayout.SOUTH);
		
		JPanel editBoxes = Util.startFrame(new GridLayout(0,1));
		JTextField jDIST = Util.addTitledFieldToPanel(editBoxes, null, "[int] Wall Distance", null);
		JTextField jHEIG = Util.addTitledFieldToPanel(editBoxes, null, "[int] Wall Height", null);
		JTextField jSIDE = Util.addTitledFieldToPanel(editBoxes, null, "[int] Wall Side", null);
		JTextField jLVSD = Util.addTitledFieldToPanel(editBoxes, null, "[int] Pattern Shape", sidesRequired + "");
		JButton jSAVE = Util.addButtonToPanel(editBoxes, null, "Save Pattern");
		
		JPattern patternCanvis = new JPattern(level, this);
		
		leftConfig.setPreferredSize(new Dimension(220,0));
		leftConfig.add(wallList);
		leftConfig.add(editBoxes);
		frame.add(leftConfig, BorderLayout.WEST);
		frame.add(patternCanvis, BorderLayout.CENTER);
		
		//Action Listeners
		addFocus(jWALL, patternCanvis, jDIST, Set.DISTANCE);
		addFocus(jWALL, patternCanvis, jHEIG, Set.HEIGHT);
		addFocus(jWALL, patternCanvis, jSIDE, Set.SIDE);
		jWALL.list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = jWALL.list.getSelectedIndex();
				if(index < 0) return; //Should not ever happen.
				Wall wall = walls.get(index);
				jDIST.setText((int)(wall.getDistance() - Wall.MIN_DISTANCE) + "");
				jHEIG.setText((int)(wall.getHeight()) + "");
				jSIDE.setText((int)(wall.getSide()) + "");
				patternCanvis.selectIndex(index);
				patternCanvis.repaint();
			}
		});
		jADDW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				walls.add(new Wall());
				Util.updateList(jWALL, walls);
				patternCanvis.repaint();
			}
		});
		jREMO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = jWALL.list.getSelectedIndex();
				if(index < 0) return;
				walls.remove(index);
				Util.updateList(jWALL, walls);
				patternCanvis.repaint();
			}
		});
		jSAVE.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sidesRequired 	= Integer.parseInt(jLVSD.getText());
					if(Util.askSave(frame) != JOptionPane.YES_OPTION) return;
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
					Element el = doc.createElement(XML_HEADER);
					writeXML(el);
					doc.appendChild(el);
					UtilXML.writeXML(file, doc);
					Util.showSuccess(frame);
				} catch (Exception ex) {
					Util.showError(frame, ex.getMessage());
					ex.printStackTrace();
				}
			}
		});
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setMinimumSize(frame.getPreferredSize());
		frame.setVisible(true);
	}

	public String toString() {
		return file.getName();
	}
	
	public Wall[] getWalls() { 
		return walls.toArray(new Wall[0]);
	}
	
	public int getSides() {
		return sidesRequired;
	}
	
	//TODO: Fix the race case when selecting the select thing.
	enum Set {DISTANCE, HEIGHT, SIDE};
	private void addFocus(ListData selected, JPattern patternCanvis, JTextField field, Set side) {
		field.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				int index = selected.list.getSelectedIndex();
				if(index < 0) return;
				int val = 1;
				try {val = Integer.parseInt(field.getText());} catch (Exception ex) {return;};	
				switch(side) {
				case DISTANCE:
					walls.get(index).setDistance(val); break;
				case HEIGHT:
					walls.get(index).setHeight(val); break;
				case SIDE:
					walls.get(index).setSide(val); break;
				}
				Util.updateList(selected, walls);
				patternCanvis.repaint();
			}
		});
	}
}
