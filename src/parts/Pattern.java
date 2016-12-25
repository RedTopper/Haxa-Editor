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
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.Level;
import data.Project;
import gfx.JPattern;
import red.Dynamic;
import red.ListData;
import red.Util;

@SuppressWarnings("serial")
public class Pattern extends JFrame{
	
	//Globals
	public static final String DIR_NAME = "patterns";
	public static final String HEADER = "PATTERN1.0";
	public static final String FOOTER = "ENDPATTERN";
	public static final String EXTENSION = ".ptn";
	
	//Locals
	public final Project project;
	public final File file;

	//Variables to write to file
	private int sidesRequired;
	private ArrayList<Wall> walls = new ArrayList<>();
	
	public Pattern(Project project, String name) {
		super("New pattern '" + name + "'");
		this.project = project;
		this.file = new File(project.dir, DIR_NAME + File.separator + name + EXTENSION);
		this.sidesRequired = 6;
	}
	
	public Pattern(Project project, File patternFile) throws IOException {
		super("Pattern '" + patternFile.getName() + "'");
		this.project = project;
		this.file = patternFile;
		ByteBuffer patternRawData = Util.readBinaryFile(patternFile);
		try {
			Util.checkString(patternRawData, HEADER);
			this.sidesRequired = patternRawData.getInt();
			loadWalls(patternRawData);
			Util.checkString(patternRawData, FOOTER);
		} catch(BufferUnderflowException e) {
			System.out.println("The file does not have all of the properties needed to create a level!");
			throw e;
		}
	}
	
	public void writeFile(Dynamic d) throws IOException {
		d.putRawString(HEADER);
		d.putInt(sidesRequired);
		d.putWalls(walls);
		d.putRawString(FOOTER);
	}
	
	public void edit(Level level) {
		setLayout(new BorderLayout());
		addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {};
			public void windowClosing(WindowEvent e) {
				level.refreshPatterns();
				level.setVisible(true);
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
		add(leftConfig, BorderLayout.WEST);
		add(patternCanvis, BorderLayout.CENTER);
		
		//Action Listeners
		addFocus(jWALL, patternCanvis, jDIST, Wall.Set.DISTANCE);
		addFocus(jWALL, patternCanvis, jHEIG, Wall.Set.HEIGHT);
		addFocus(jWALL, patternCanvis, jSIDE, Wall.Set.SIDE);
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
					if(Util.askSave(Pattern.this) != JOptionPane.YES_OPTION) return;
					Dynamic d = new Dynamic();
					writeFile(d);
					d.write(file);
					Util.showSuccess(Pattern.this);
				} catch (Exception ex) {
					Util.showError(Pattern.this, ex.getMessage());
					ex.printStackTrace();
				}
			}
		});
		
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setMinimumSize(getPreferredSize());
		setVisible(true);
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

	private void loadWalls(ByteBuffer patternRawData) {
		int numberOfWalls = patternRawData.getInt();
		for(int i = 0; i < numberOfWalls; i++) {
			walls.add(new Wall(patternRawData.getChar(), patternRawData.getChar(), patternRawData.getChar()));
		}
	}
	
	//TODO: Fix the race case when selecting the select thing.
	private void addFocus(ListData selected, JPattern patternCanvis, JTextField field, Wall.Set side) {
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
