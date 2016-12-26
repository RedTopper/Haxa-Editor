package red;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import parts.Wall;

public class UtilXML {
	
	public static final String XML_COLOR = "Color";
	public static final String XML_RED = "Red";
	public static final String XML_GREEN = "Green";
	public static final String XML_BLUE = "Blue";
	
	
	private UtilXML() throws InstantiationException {
		throw new InstantiationException();
	}
	
	/**
	 * Opens an XML document by checking it's root element.
	 * @param patternFile File to open.
	 * @param type Type of the file as a string.
	 * @return A document
	 * @throws Exception
	 */
	public static Document openXML(File file, String type) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		if(!doc.getDocumentElement().getNodeName().equals(type)) throw new Exception("Failed to open file!");
		return doc;
	}
	
	/**
	 * Writes a document to a file.
	 * @param file The file to write.
	 * @param doc The document to write.
	 * @throws Exception
	 */
	public static void writeXML(File file, Document doc) throws Exception{
		File oldDir = Util.getDir(new File(file.getParent() + Util.OLD));
		File backupFile = new File(oldDir, file.getName());
		backupFile.delete();
		file.renameTo(backupFile);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	    DOMSource source = new DOMSource(doc);
	    StreamResult console = new StreamResult(file);
	    transformer.transform(source, console);
	    
	    System.out.println("Backed up and wrote XML file: '" + file.getAbsolutePath() + "'");
	}
	
	public static int getInt(Element root, String name) {
		try {
			Element e = (Element)root.getElementsByTagName(name).item(0);
			if(e != null) return Integer.parseInt(e.getTextContent());
		} catch(Exception e) {}
		return 0;
	}

	public static void putInt(Element root, String name, int i) {
		Element e = root.getOwnerDocument().createElement(name);
		e.appendChild(root.getOwnerDocument().createTextNode(i + ""));
		root.appendChild(e);
	}

	public static float getFloat(Element root, String name) {
		try {
			Element e = (Element)root.getElementsByTagName(name).item(0);
			if(e != null) return Float.parseFloat(e.getTextContent());
		} catch(Exception e) {}
		return 0.0f;
	}

	public static void putFloat(Element root, String name, float f) {
		Element e = root.getOwnerDocument().createElement(name);
		e.appendChild(root.getOwnerDocument().createTextNode(f + ""));
		root.appendChild(e);
	}

	public static String getString(Element root, String name) {
		try {
			Element e = (Element)root.getElementsByTagName(name).item(0);
			if(e != null) return e.getTextContent();
		} catch(Exception e) {}
		return "NOT SET";
	}

	public static void putString(Element root, String name, String string) {
		Element e = root.getOwnerDocument().createElement(name);
		e.appendChild(root.getOwnerDocument().createTextNode(string));
		root.appendChild(e);
	}

	public static Color getColor(Element root) {
		return new Color(getInt(root, XML_RED), getInt(root, XML_GREEN), getInt(root, XML_BLUE));
	}

	public static void putColor(Element root, Color color) {
		Element c = root.getOwnerDocument().createElement(XML_COLOR);
		putInt(c, XML_RED, color.getRed());
		putInt(c, XML_GREEN, color.getGreen());
		putInt(c, XML_BLUE, color.getBlue());
		root.appendChild(c);
	}

	public static List<Color> getColors(Element root, String name) {
		List<Color> colors = new ArrayList<>();
		Element block = (Element)root.getElementsByTagName(name).item(0);
		if(block == null) return colors;
		NodeList ecolors = block.getElementsByTagName(XML_COLOR);
		if(ecolors == null) return colors;
		for(int i = 0; i < ecolors.getLength(); i++) {
			Element color = (Element)ecolors.item(i);
			if(color == null) continue;
			colors.add(getColor(color));
		}
		return colors;
	}

	public static void putColors(Element root, String name, List<Color> colors) {
		Element e = root.getOwnerDocument().createElement(name);
		for(Color c : colors) UtilXML.putColor(e, c);
		root.appendChild(e);
	}

	public static List<Wall> getWalls(Element root) {
		List<Wall> walls = new ArrayList<>();
		NodeList ewalls = root.getElementsByTagName(Wall.XML_HEADER);
		if(ewalls == null) return walls;
		for(int i = 0; i < ewalls.getLength(); i++) {
			Element wall = (Element)ewalls.item(i);
			if(wall == null) continue;
			walls.add(new Wall(wall));
		}
		return walls;
	}

	public static void putWalls(Element root, List<Wall> walls) {
		Element e = root.getOwnerDocument().createElement(Wall.XML_HEADER);
		for(Wall w : walls) w.writeXML(e);
		root.appendChild(e);
	}
}
