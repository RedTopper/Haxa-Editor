package data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import parts.Pattern;
import red.Dynamic;
import red.ListData;
import red.Util;
import red.UtilXML;

public class Level {
	
	//Files
	public static final String XML_HEADER = "Level";
	public static final String BIN_HEADER = "LEV2.1";
	public static final String BIN_FOOTER = "ENDLEV";
	
	//Finals
	public final Project project;
	public final File file;
	public final JFrame frame;
	private ListData jpatterns;
	
	public Level(Project project, String name) {
		this.frame = new JFrame("New level '" + name + "'");
		this.project = project;
		this.file = new File(project.dir, name.replaceAll("\\s", "") + ".xml");
		setDefaults();
		this.name = name.toUpperCase();
	}
	
	public Level(Project project, File levelFile)  {
		this.frame = new JFrame("Level '" + levelFile.getName() + "'");
		this.project = project;
		this.file = levelFile;
		setDefaults();
		try {
			Document doc = UtilXML.openXML(levelFile, XML_HEADER);
			readXML(doc.getDocumentElement());
		}  catch(Exception e) {
			System.out.println("Failure to parse level file " + toString() + "! Using default level information.");
		}
	}
	
	//READ AND WRITE VARIABLES
	public static final String XML_NAME = "Name";
	public static final String XML_DIFFICULTY = "Difficulty";
	public static final String XML_MODE = "Mode";
	public static final String XML_CREATOR = "Creator";
	public static final String XML_MUSIC = "Music";
	public static final String XML_BG1 = "BackgroundColorOne";
	public static final String XML_BG2 = "BackgroundColorTwo";
	public static final String XML_FG = "ForegroundColor";
	public static final String XML_SPEED_WALL = "SpeedWall";
	public static final String XML_SPEED_ROTATION = "SpeedRotation";
	public static final String XML_SPEED_CURSOR = "SpeedCursor";
	public static final String XML_SPEED_PULSE = "SpeedPulse";
	public static final String XML_PATTERNS = "LinkedPatterns";
	public static final String XML_PATTERN = "Pattern";
	private String name;
	private String difficulty;
	private String mode;
	private String creator;
	private String music;
	private float speedWall;
	private float speedRotation;
	private float speedCursor;
	private int speedPulse;
	private List<Color> bg1;
	private List<Color> bg2;
	private List<Color> fg;
	private List<Pattern> patterns;
	
	private void setDefaults() {
		this.name 			= "VOID";
		this.difficulty		= "UNKNOWN";
		this.mode 			= "NORMAL";
		this.creator 		= "ANONYMOUS";
		this.music 			= "NONE";
		this.speedWall 		= 2.0f;
		this.speedRotation 	= (float)(Math.PI  * 2.0) / 120.0f;
		this.speedCursor	= (float)(Math.PI  * 2.0) / 60.0f;
		this.speedPulse 	= 120;
		this.bg1			= new ArrayList<>();
		this.bg2			= new ArrayList<>();
		this.fg 			= new ArrayList<>();
		this.patterns 		= new ArrayList<>();
		this.bg1.add(Color.BLACK);
		this.bg2.add(Color.GRAY);
		this.fg.add(Color.WHITE);
	}
	
	public void readXML(Element e) throws Exception {
		this.name 			= UtilXML.getString(e, XML_NAME);
		this.difficulty 	= UtilXML.getString(e, XML_DIFFICULTY);
		this.mode 			= UtilXML.getString(e, XML_MODE);
		this.creator 		= UtilXML.getString(e, XML_CREATOR);
		this.music 			= UtilXML.getString(e, XML_MUSIC);
		this.speedWall 		= UtilXML.getFloat(e, XML_SPEED_WALL);
		this.speedRotation 	= UtilXML.getFloat(e, XML_SPEED_ROTATION);
		this.speedCursor	= UtilXML.getFloat(e, XML_SPEED_CURSOR);
		this.speedPulse 	= UtilXML.getInt(e, XML_SPEED_PULSE);
		this.bg1			= UtilXML.getColors(e, XML_BG1);
		this.bg2			= UtilXML.getColors(e, XML_BG2);
		this.fg				= UtilXML.getColors(e, XML_FG);
		this.patterns 		= getPatterns(e);
	}

	public void writeXML(Element e) {
		UtilXML.putString(e, XML_NAME, name);
		UtilXML.putString(e, XML_DIFFICULTY, difficulty);
		UtilXML.putString(e, XML_MODE, mode);
		UtilXML.putString(e, XML_CREATOR, creator);
		UtilXML.putString(e, XML_MUSIC, music);
		UtilXML.putFloat(e, XML_SPEED_WALL, speedWall);
		UtilXML.putFloat(e, XML_SPEED_ROTATION, speedRotation);
		UtilXML.putFloat(e, XML_SPEED_CURSOR, speedCursor);
		UtilXML.putInt(e, XML_SPEED_PULSE, speedPulse);
		UtilXML.putColors(e, XML_BG1, bg1);
		UtilXML.putColors(e, XML_BG2, bg2);
		UtilXML.putColors(e, XML_FG, fg);
		Element ptns = e.getOwnerDocument().createElement(XML_PATTERNS);
		for(Pattern p : patterns) UtilXML.putString(ptns, XML_PATTERN, p.toString());
		e.appendChild(ptns);
	}
	
	public void writeBIN(Dynamic d) throws IOException {
		d.putRawString(BIN_HEADER);
		d.putString(name);
		d.putString(difficulty);
		d.putString(mode);
		d.putString(creator);
		d.putString(music);
		d.putColors(bg1);
		d.putColors(bg2);
		d.putColors(fg);
		d.putFloat(speedWall);
		d.putFloat(speedRotation);
		d.putFloat(speedCursor);
		d.putInt(speedPulse);
		d.putInt(patterns.size());
		for(Pattern p : patterns) d.putString(p.toString());
		d.putRawString(BIN_FOOTER);
	}

	public void edit() {
		frame.setLayout(new GridLayout(1,0));
		frame.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {};
			public void windowClosing(WindowEvent e) {
				project.loadLevels();
				project.frame.setVisible(true);
			}
		});
		
		//BEGIN Left Side Textual Configuration
		JPanel textConfiguration = Util.startFrame(new GridLayout(0,1));
		JTextField jNAME = Util.addTitledFieldToPanel(textConfiguration, null, "[String] Name", name);
		JTextField jDIFF = Util.addTitledFieldToPanel(textConfiguration, null, "[String] Difficulty", difficulty);
		JTextField jMODE = Util.addTitledFieldToPanel(textConfiguration, null, "[String] Mode", mode);
		JTextField jCREA = Util.addTitledFieldToPanel(textConfiguration, null, "[String] Creator", creator);
		JTextField jMUSE = Util.addTitledFieldToPanel(textConfiguration, null, "[String] Music File + extension", music);
		JTextField jWALL = Util.addTitledFieldToPanel(textConfiguration, null, "[float] Wall Speed", speedWall + "");
		JTextField jROTA = Util.addTitledFieldToPanel(textConfiguration, null, "[TAU/float] Rotation Step", speedRotation + "");
		JTextField jCURS = Util.addTitledFieldToPanel(textConfiguration, null, "[TAU/float] Human Step", speedCursor + "");
		JTextField jPLUS = Util.addTitledFieldToPanel(textConfiguration, null, "[float] Pulse Speed", speedPulse + "");
		JButton jSAVE = Util.addButtonToPanel(textConfiguration, null, "Save Configuration");
		frame.add(textConfiguration);
		
		//BEGIN Color Panel
		JPanel colors = Util.startFrame(new GridLayout(0,1));
		Util.createColorPicker(colors, bg1, "Background Primary");
		Util.createColorPicker(colors, bg2, "Background Secondary");
		Util.createColorPicker(colors, fg, "Foreground");
		frame.add(colors);
		
		//BEGIN Right Side pattern chooser
		JPanel patternConfiguration = Util.startFrame(new GridLayout(0,1));
		
		//BEGIN Top Pattern Selector
		JPanel patternsAvailable = Util.startFrame(new BorderLayout());
		jpatterns = Util.addTitledListToPanel(patternsAvailable, BorderLayout.CENTER, "Available Patterns", project.getPatterns());
		JButton jPATT = Util.addButtonToPanel(patternsAvailable, BorderLayout.NORTH, "Create new pattern");
		JButton jEDIT = Util.addButtonToPanel(patternsAvailable, BorderLayout.SOUTH, "Edit selected available pattern");
		patternConfiguration.add(patternsAvailable);
		
		//BEGIN Bottom Level Patterns
		JPanel patternLevel = Util.startFrame(new BorderLayout());
		ListData jLEVE = Util.addTitledListToPanel(patternLevel, BorderLayout.CENTER, "Linked Patterns", patterns);
		JButton jLINK = Util.addButtonToPanel(patternLevel, BorderLayout.NORTH, "Link available pattern to this level");
		JButton jUNLK = Util.addButtonToPanel(patternLevel, BorderLayout.SOUTH,"Unlink pattern from this level");
		patternConfiguration.add(patternLevel);
		frame.add(patternConfiguration);
		
		//Action Listeners
		jSAVE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					name 			= Util.upperText(jNAME);
					difficulty 		= Util.upperText(jDIFF);
					mode 			= Util.upperText(jMODE);
					creator 		= Util.upperText(jCREA);
					music 			= Util.getText(jMUSE);
					speedWall 		= Float.parseFloat(jWALL.getText());
					speedRotation 	= Float.parseFloat(jROTA.getText());
					speedCursor 	= Float.parseFloat(jCURS.getText());
					speedPulse 		= Integer.parseInt(jPLUS.getText());
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
		jPATT.addActionListener(new  ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String patternName = JOptionPane.showInputDialog(frame, 
					"What do you want to call this pattern?", 
					"Pattern File Name", JOptionPane.QUESTION_MESSAGE);
				if(patternName == null || patternName.length() == 0) return;
				frame.setVisible(false);
				new Pattern(project, patternName).edit(Level.this);
			}
		});
		jEDIT.addActionListener(new  ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selection = jpatterns.list.getSelectedIndex();
				if(selection < 0) return;
				frame.setVisible(false);
				project.getPatterns().get(selection).edit(Level.this);;
			}
		});
		jLINK.addActionListener(new  ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selection = jpatterns.list.getSelectedIndex();
				if(selection < 0) return;
				patterns.add(project.getPatterns().get(selection));
				Util.updateList(jLEVE, patterns);
			}
		});
		jUNLK.addActionListener(new  ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selection = jLEVE.list.getSelectedIndex();
				if(selection < 0) return;
				patterns.remove(selection);
				Util.updateList(jLEVE, patterns);
			}
		});
		
		//bring window to life!
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setMinimumSize(frame.getPreferredSize());
		frame.setVisible(true);
	}
	
	public String toString() {
		return file.getName();
	}
	
	public List<Color> getBG1() {
		return bg1;
	}
	
	public List<Color> getBG2() {
		return bg2;
	}
	
	public List<Color> getFG() {
		return fg;
	}
	
	public void refreshPatterns() {
		project.loadPatterns();
		Util.updateList(jpatterns, project.getPatterns());
	}
	
	private List<Pattern> getPatterns(Element root) {
		List<Pattern> patterns = new ArrayList<>();
		Element block = (Element)root.getElementsByTagName(XML_PATTERNS).item(0);
		if(block == null) return patterns;
		NodeList epatterns = block.getElementsByTagName(XML_PATTERN);
		for(int i = 0; i < epatterns.getLength(); i++) {
			String bindName = ((Element)epatterns.item(i)).getTextContent();
			for(Pattern pattern : project.getPatterns()) {
				if(pattern.toString().equals(bindName)) patterns.add(pattern);
			}
		}
		return patterns;
	}
}
