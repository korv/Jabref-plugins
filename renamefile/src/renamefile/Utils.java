package renamefile;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexFields;
import net.sf.jabref.Globals;
import net.sf.jabref.JabRefFrame;
import net.sf.jabref.MetaData;
import net.sf.jabref.Util;
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
			ExternalFileType type =  Globals.prefs.getExternalFileTypeByExt(getExt(fn));
			if(type==null && getExt(fn).equalsIgnoreCase("gz"))
				type =  Globals.prefs.getExternalFileTypeByExt("ps");
			FileListEntry e = new FileListEntry("",fn,type);
			m.addEntry(0,e);
			b.setField("file",m.getStringRepresentation());
			panel.markBaseChanged();
		}
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
		panel.markBaseChanged();
	}

	public static void removeFile(BibtexEntry b, String fn) {
		if(!hasFile(b,fn))
			return;
		removeFileEntry(b,fn);
		String dir = getFileDir();
		File f=Util.expandFilename(fn,dir);
		if (f!=null)
			f.delete();
	}

	public static String getFileDir() {
		String dir = null;
		if (panel != null) {
			MetaData md = panel.metaData();
			if (md != null) {
				dir = md.getFileDirectory("file");
				if (dir != null && dir.length() != 0)
					return dir;
				dir = md.getFileDirectory("pdf");
				if (dir != null && dir.length() != 0)
					return dir;
			}
		}
		return null;
	}

	public static String getNewFileName(String ofn,BibtexEntry b){
		boolean sameDir=Globals.prefs.getBoolean("SameFolder");
		boolean sameName=Globals.prefs.getBoolean("SameName");
		String dir=Globals.prefs.get("MoveFolder");
		String pattern=Globals.prefs.get("RenamePattern");
		return getNewFileName(ofn, b, sameDir, sameName,	dir, pattern);
	}

	public static String getNewFileName(String ofn,BibtexEntry b,boolean sameDir,boolean sameName,
			String dir,String pattern){
		String s = System.getProperty("file.separator");
		String fn;
		if(sameName) {
			if(sameDir)
				return ofn;
			else
				fn=ofn.substring(ofn.lastIndexOf(s)+1,ofn.length());
		} else
			fn=makeLabel(pattern,b)+"."+getExt(ofn);
		if(sameDir)
			return ofn.substring(0,ofn.lastIndexOf(s)+1)+fn;
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
		return f.substring(f.lastIndexOf('.')+1, f.length());
	}

	public static String makeLabel(String pattern, BibtexEntry _entry) {
		String label;
		StringBuffer sb = new StringBuffer();
		ArrayList<String> t = LabelPatternUtil.split(pattern);
		boolean field = false, first=true;
		for (String i:t) {
			if(first) {
				first=false;
				continue;
			}
			if (i.equals("[")) {
				field = true;
			} else if (i.equals("]")) {
				field = false;
			} else if (field) {
				String[] parts = LabelPatternUtil.parseFieldMarker(i);
				label = LabelPatternUtil.makeLabel(_entry, parts[0]);
				if (parts.length > 1)
					label = LabelPatternUtil.applyModifiers(label, parts, 1);
				sb.append(label);
			} else {
				sb.append(i);
			}
		}
		label = sb.toString();
		String[] bad={"\\\\","<",">","\\?","\\{","\\}","\\$","\"","\n",":"};
		for(String s:bad)
			label=label.replaceAll(s,"");
		//		String regex = Globals.prefs.get("KeyPatternRegex");
		//		if ((regex != null) && (regex.trim().length() > 0)) {
		//			String replacement = Globals.prefs.get("KeyPatternReplacement");
		//			label = label.replaceAll(regex, replacement);
		//		}
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
	
}
