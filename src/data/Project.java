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

@SuppressWarnings("serial")
public class Project extends JFrame{
	public static final String HEADER = "HAXAGON1.0";
	public static final String NAME = "levels.haxagon";
	public final File dir;
	
	private ArrayList<Level> levels = null;
	private ArrayList<Pattern> patterns = null;
	private ListData list = null;
	
	public Project(File projectDir) {
		super("Open Level");
		this.dir = projectDir;
		loadPatterns();
		loadLevels();
		setMinimumSize(new Dimension(245, 350));
		setLayout(new GridLayout(0,1));
	}
	
	public void edit() {
		JPanel contents = Util.startFrame(new BorderLayout());
		list = Util.addTitledListToPanel(contents, BorderLayout.CENTER, "Available Levels", levels);
		JButton jCREA = Util.addButtonToPanel(contents, BorderLayout.NORTH, "Create new level");
		JPanel buttons = Util.startFrame(new BorderLayout());
		JButton jEDIT = Util.addButtonToPanel(buttons, BorderLayout.NORTH, "Edit selected level");
		JButton jEXPO = Util.addButtonToPanel(buttons, BorderLayout.SOUTH, "Export all levels to game");
		contents.add(buttons, BorderLayout.SOUTH);
		add(contents);
		
		jCREA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(Project.this, 
					"What do you want to call this level?", 
					"Level Name", JOptionPane.QUESTION_MESSAGE);
				if(name == null || name.length() == 0) return;
				Project.this.setVisible(false);
				new Level(Project.this, name.trim()).edit();
			}
		});
		jEDIT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = list.list.getSelectedIndex();
				if(index < 0) return;
				Project.this.setVisible(false);
				levels.get(index).edit();
			}
		});
		jEXPO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int result = JOptionPane.showOptionDialog(Project.this, 
						Util.SAVE_QUESTION_MESSAGE, Util.SAVE_QUESTION_TITLE, JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
					if(result != JOptionPane.YES_OPTION) return;
					Dynamic d = new Dynamic();
					d.putInt(patterns.size());
					for(Pattern p : patterns) {
						d.putString(p.toString());
						p.writeFile(d);
					}
					d.putInt(levels.size());
					for(Level l : levels) l.writeFile(d);
					d.write(new File(new File(dir, ".." + File.separator), NAME));
					JOptionPane.showMessageDialog(Project.this, 
						Util.SAVE_SUCCESS_MESSAGE, Util.SAVE_SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(Project.this, 
						Util.SAVE_FAIL_MESSAGE, Util.SAVE_FAIL_TITLE, JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		});
		
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
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
