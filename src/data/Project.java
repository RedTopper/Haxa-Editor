package data;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import parts.Pattern;
import red.Dynamic;
import red.ListData;
import red.Util;

public class Project {
	public static final String BIN_HEADER = "HAX1.0";
	public static final String BIN_FOOTER = "ENDHAX";
	public static final String NAME = "levels.haxagon";
	public final File dir;
	public final JFrame frame;
	
	private ArrayList<Level> levels = null;
	private ArrayList<Pattern> patterns = null;
	private ListData list = null;
	
	public Project(File projectDir) {
		frame = new JFrame("Open Level");
		this.dir = projectDir;
		loadPatterns();
		loadLevels();
		frame.setMinimumSize(new Dimension(245, 350));
		frame.setLayout(new GridLayout(0,1));
	}
	
	public void edit() {
		JPanel contents = Util.startFrame(new BorderLayout());
		list = Util.addTitledListToPanel(contents, BorderLayout.CENTER, "Available Levels", levels);
		JButton jCREA = Util.addButtonToPanel(contents, BorderLayout.NORTH, "Create new level");
		JPanel buttons = Util.startFrame(new BorderLayout());
		JButton jEDIT = Util.addButtonToPanel(buttons, BorderLayout.NORTH, "Edit selected level");
		JButton jEXPO = Util.addButtonToPanel(buttons, BorderLayout.SOUTH, "Export all levels to game");
		contents.add(buttons, BorderLayout.SOUTH);
		frame.add(contents);
		
		jCREA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(frame, 
					"What do you want to call this level?", 
					"Level Name", JOptionPane.QUESTION_MESSAGE);
				if(name == null || name.length() == 0) return;
				frame.setVisible(false);
				new Level(Project.this, name.trim()).edit();
			}
		});
		jEDIT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = list.list.getSelectedIndex();
				if(index < 0) return;
				frame.setVisible(false);
				levels.get(index).edit();
			}
		});
		jEXPO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(Util.askSave(frame) != JOptionPane.YES_OPTION) return;
					Dynamic d = new Dynamic();
					d.putRawString(BIN_HEADER);
					d.putInt(patterns.size());
					for(Pattern p : patterns) {
						d.putString(p.toString());
						p.writeBIN(d);
					}
					d.putInt(levels.size());
					for(Level l : levels) l.writeBIN(d);
					d.putRawString(BIN_FOOTER);
					d.write(new File(new File("."), NAME));
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
		frame.setVisible(true);
	}

	public void loadLevels() {
		System.out.println("Refreshing level list...");
		levels = new ArrayList<>();
		ArrayList<File> levelFiles = Util.getDirContents(Util.FileType.FILE, dir, "FOUND LEVELS", "NO LEVELS FOUND");
		for(File levelFile : levelFiles) {
			try {
				levels.add(new Level(this, levelFile));
			} catch (Exception e) {
				System.out.println("Something went wrong when loading a level!");
				e.printStackTrace();
			}
		}
		if(list == null) return;
		Util.updateList(list, levels);
	}

	public void loadPatterns() {
		System.out.println("Refreshing patterns...");
		patterns = new ArrayList<>();
		File patternFolder = Util.getDir(new File(dir, Pattern.DIR_NAME));
		ArrayList<File> patternFiles = Util.getDirContents(Util.FileType.FILE, patternFolder, "FOUND PATTERNS", "NO PATTERNS FOUND");
		for(File patternFile : patternFiles) {
			try {
				patterns.add(new Pattern(Project.this, patternFile));
			} catch (Exception e) {
				System.out.println("Something went wrong when loading a pattern!");
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<Pattern> getPatterns() {
		return patterns;
	}
}
