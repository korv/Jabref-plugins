package renamefile;


import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

import javax.swing.*;

import net.sf.jabref.*;
import net.sf.jabref.external.ExternalFileType;
import net.sf.jabref.gui.FileListEntry;
import net.sf.jabref.gui.FileListTableModel;
import net.sf.jabref.labelPattern.LabelPatternUtil;

public class Utils {
	private static JabRefFrame frame;
	private static Method setDataBase = null;

	public static void init(JabRefFrame f){
		frame=f;
		Class c;
		try {
			c = Class.forName("net.sf.jabref.labelPattern.LabelPatternUtil");
			Method[] methods = c.getMethods();
			for (Method m : methods)
				if (m.getName().equals("setDataBase")) 
					setDataBase = m;
		}
		catch (Exception e) {}
	}

	private static BasePanel panel(){
		return frame.basePanel();
	}
	public static void addFileEntry(BibtexEntry b, String fn) {
		if (!hasFile(b,fn)) {
			FileListTableModel m = new FileListTableModel();
			m.setContent(b.getField("file"));
			ExternalFileType type = getExternalFileTypeForName(fn);
//2.6			ExternalFileType type = Globals.prefs.getExternalFileTypeByName(getExt(fn));
			FileListEntry e = new FileListEntry("",fn,type);
			m.addEntry(m.getRowCount(),e);
			b.setField("file",m.getStringRepresentation());
		}
	}
	
    private static ExternalFileType getExternalFileTypeForName(String filename) {
    	// Code from Global.prefs.getExternalFileTypeForName() in JabRef 2.7+
    	ExternalFileType[] types=Globals.prefs.getExternalFileTypeSelection();
        int longestFound = -1;
        ExternalFileType foundType = null;
        for (ExternalFileType type:types){
            if ((type.getExtension() != null) && filename.toLowerCase().
                    endsWith(type.getExtension().toLowerCase())) {
                if (type.getExtension().length() > longestFound) {
                    longestFound = type.getExtension().length();
                    foundType = type;
                }
            }
        }
        return foundType;
    }

    public static boolean hasFile(BibtexEntry b, String fn) {
		FileListTableModel m = new FileListTableModel();
		m.setContent(b.getField("file"));
		for (int j=0;j<m.getRowCount();j++) {
			FileListEntry f = m.getEntry(j);
			if (f.getLink().equals(fn))
				return true;
		}
		return false;
	}

	public static void removeFileEntry(BibtexEntry b, String fn) {
		FileListTableModel m = new FileListTableModel();
		m.setContent(b.getField("file"));
		for (int j=m.getRowCount()-1;j>=0;j--) {
			FileListEntry f = m.getEntry(j);
			if (f.getLink().equals(fn))
				m.removeEntry(j);
		}
		b.setField("file",m.getStringRepresentation());
	}

	// code from Metadata.getFileDirectory	
	public static String getFileDir() {
		if(panel()==null || panel().metaData()==null)
			return null;
		MetaData md=panel().metaData();
		File file = md.getFile(); //bib file
		// Check if the bib file location is set and primary:
		if (Globals.prefs.getBoolean("bibLocationAsFileDir") 
				&& Globals.prefs.getBoolean("bibLocAsPrimaryDir")
				&& file != null) {
			return file.getParent();
		}
		String dir;
		String key = Globals.prefs.get("userFileDirIndividual");
		Vector<String> vec = md.getData(key);
		if (vec == null) {
			key = Globals.prefs.get("userFileDir");
			vec = md.getData(key);
		}
		if (vec == null)
			vec = md.getData("fileDirectory"); //2.6?
		if ((vec != null) && (vec.size() > 0))
			dir = vec.get(0);
		else
			//This is a global file directory
			//In the original code it is called by get(field+"Directory")
			dir = Globals.prefs.get("fileDirectory");
		if (dir==null)
			dir="";
		// If this directory is relative, we try to interpret it as relative to
		// the file path of this bib file:
		if (!(new File(dir)).isAbsolute() && (file != null)) {
			String relDir;
			if (dir.equals(".")) {
				relDir = file.getParent(); 
			} else {
				relDir = new StringBuffer(file.getParent()).
						append(System.getProperty("file.separator")).
						append(dir).toString();
			}
			// If this directory actually exists, it is very likely that the
			// user wants us to use it:
			if ((new File(relDir)).exists())
				dir = relDir;
		}
		return dir;
	}


	public static String getNewFileName(String ofn,BibtexEntry b){
		boolean sameDir=Globals.prefs.getBoolean("SameFolder");
		boolean sameName=Globals.prefs.getBoolean("SameName");
		String dir=Globals.prefs.get("MoveFolder");
		String pattern=Globals.prefs.get("RenamePattern");
		return getNewFileName(ofn, b, sameDir, sameName, dir, pattern);
	}

	public static String getNewFileName(String ofn,BibtexEntry b,boolean sameDir,boolean sameName,
			String dir,String pattern){
		final String s = System.getProperty("file.separator");
		String fn;
		if(sameName) {
			if(sameDir)
				return ofn;
			else
				fn=ofn.substring(ofn.lastIndexOf(s)+1,ofn.length());
		} else
			fn=makeLabel(pattern,b)+"."+getExt(ofn);
		if(sameDir)
			return fixSlash(ofn.substring(0,ofn.lastIndexOf(s)+1)+fn);
		if(dir.isEmpty())
			return fixSlash(fn);
		if (dir.endsWith(s))
			return fixSlash(dir + fn);
		else
			return fixSlash(dir+s+fn);
	}

	private static String fixSlash(String s){
		if (Globals.ON_WIN) 
			s = s.replaceAll("/", "\\\\");
		else
			s = s.replaceAll("\\\\", "/");
		return s;
	}

	private static String getExt(String f){
		return f.substring(f.lastIndexOf('.')+1);
	}

	public static boolean haveSameExtensions(String f1,String f2){
		return getExt(f1).equalsIgnoreCase(getExt(f2));
	}
	
	public static String makeLabel(String pattern, BibtexEntry _entry) {
		// Needed only in 2.10+ as LabelPatternUtil.makeLabel uses the DataBase (private var _db) starting from 2.10:
		// The public method setDataBase() is defined only starting from 2.10
		// We look for setDataBase during the initialization
		try {
			if (setDataBase!=null)
					setDataBase.invoke(null, new Object[] {panel().database()});
		}
		catch (Exception e) {}

		final String[] subst = {
				"\\\\([oilL])[\\s\\}]+", "\\\\a(a)[\\s\\}]+", "\\\\A(A)[\\s\\}]+", "\\\\(ss)[\\s\\}]+", //symbols
				"\\\\[a-zA-Z]+[\\s\\{\\}\\\\]+()", //latex commands
				"\\\\[^\\w][\\s\\{]*([a-zA-z])" //accents
		};
		final String[] bad_chars = {"\\\\","/","<",">","\\?","\\{","\\}","\\$","\"","\n",":"};
		StringBuffer sb = new StringBuffer();
		String label;
		boolean field = false;
		ArrayList<String> lst=LabelPatternUtil.split(pattern);
		for (int i=1; i<lst.size(); i++) {
			String s=lst.get(i);
			if (s.equals("["))
				field = true;
			else if (s.equals("]"))
				field = false;
			else if (field) {
				String[] parts = LabelPatternUtil.parseFieldMarker(s);
				String val = parts[0];
				if (val!=null & val.equals("type")) {
					BibtexEntryType o = _entry.getType();
					label = (o!=null ? o.getName():"");
				} else
					label = LabelPatternUtil.makeLabel(_entry, val);
				label = applyModifiers(label, parts);
				for (String c: subst)
					label=label.replaceAll(c,"$1");					
				for(String c: bad_chars)
					label=label.replaceAll(c,"");
				sb.append(label);
			} else
				sb.append(s);
		}
		label = sb.toString();
		return label;
	}
	
	
	private static String applyModifiers(String label, String[] parts) {
		for (int j = 1; j < parts.length; j++) {
			String modifier = parts[j];
			if (modifier.equals("lower")) {
				label = label.toLowerCase();
			} else if (modifier.equals("upper")) {
				label = label.toUpperCase();
			} else if (modifier.equals("abbr")) {
				StringBuffer abbr = new StringBuffer();
				String[] words = label.toString().replaceAll("[\\{\\}']","")
						.split("[\\(\\) \r\n\"]");
				for (String word: words)
					if (!word.isEmpty())
						abbr.append(word.charAt(0));
				label = abbr.toString();
			} else if (modifier.equals("regex") && j+2<parts.length) {
				label = label.replaceAll(parts[j+1],parts[j+2]);
				j = j + 2;
			} else {
				Globals.logger("File name generator warning: unknown modifier '"
						+ modifier + "'.");
			}
		}
		return label;
	}


	public static boolean copyFile(File of,File nf){
		InputStream in;
		try {
			in = new FileInputStream(of);
			OutputStream out = new FileOutputStream(nf);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
    public static void displayAbout(JFrame parent) {
		InputStream stream = Utils.class.getResourceAsStream("/about.txt");
		String text=streamToString(stream);
        JTextArea ta = new JTextArea(text);
        ta.setEditable(false);
        ta.setBackground(Color.white);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(620,500));
        JOptionPane.showMessageDialog(parent,sp,
                "About Renamefile plugin",JOptionPane.PLAIN_MESSAGE);
    }
	
	public static String streamToString(InputStream is) {
		final int len=0x10000;
		final char[] buffer = new char[len];
		StringBuilder out = new StringBuilder();
		try{
			Reader in = new InputStreamReader(is,"UTF-8");
			int read;
			while((read=in.read(buffer,0,len))!=-1)
				out.append(buffer,0,read);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}

}
