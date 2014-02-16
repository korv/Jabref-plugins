package renamefile;


import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import net.sf.jabref.*;
import net.sf.jabref.external.ExternalFileType;
import net.sf.jabref.gui.FileListEntry;
import net.sf.jabref.gui.FileListTableModel;
import net.sf.jabref.labelPattern.LabelPatternUtil;

public class Utils {
	private static BasePanel panel;

	public static void init(JabRefFrame frame){
		panel=frame.basePanel();
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

	public static String getFileDir() {
		if(panel==null || panel.metaData()==null)
			return null;
		MetaData md=panel.metaData();
		
//Code from MetaData.getFileDirectory()
		String key = Globals.prefs.get("userFileDirIndividual");
        if(md.getData(key)==null)
//2.6+
        	key = "fileDirectory";
//2.7+     	key = Globals.prefs.get("userFileDir");
        Vector<String> vec = md.getData(key);
        String dir;
        if (vec!= null && !vec.isEmpty())
        	dir = vec.get(0);
        else
        	dir = Globals.prefs.get("fileDirectory");
        if (!(new File(dir)).isAbsolute() && (md.getFile() != null))
        	dir = new StringBuffer(md.getFile().getParent()).
        	append(System.getProperty("file.separator")).
        	append(dir).toString();
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
		String label;
		StringBuffer sb = new StringBuffer();
		String[] bad_chars={"\\\\","/","<",">","\\?","\\{","\\}","\\$","\"","\n",":"};
		boolean field = false, first =true;
		for (String i : LabelPatternUtil.split(pattern)) {
			if (first) {
				first=false;
				continue;
			}
			if (i.equals("["))
				field = true;
			else if (i.equals("]"))
				field = false;
			else if (field) {
				String[] parts = LabelPatternUtil.parseFieldMarker(i);
				String val = parts[0];
				if (val!=null & val.equals("type")) {
					BibtexEntryType o = _entry.getType();
					label = (o!=null ? o.getName():"");
				} else
					label = LabelPatternUtil.makeLabel(_entry, val);
				if (parts.length > 1)
					label = LabelPatternUtil.applyModifiers(label, parts, 1);
				for(String s:bad_chars)
					label=label.replaceAll(s,"");
				sb.append(label);
			} else {
				sb.append(i);
			}
		}
		label = sb.toString();
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
