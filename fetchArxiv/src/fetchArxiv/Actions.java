package fetchArxiv;


import javax.swing.JOptionPane;

import net.sf.jabref.*;
import net.sf.jabref.imports.OAI2Fetcher;

public class Actions {
	private BasePanel panel;
	private JabRefFrame frame;

	public Actions(JabRefFrame frame){
		this.frame=frame;
		panel=frame.basePanel();
	}

	public void actionNew(){
		getInfo(null);
	}

	public void actionInfo(){
		BibtexEntry[] bes = panel.mainTable.getSelectedEntries();
		for(BibtexEntry b:bes)
			getInfo(b);
	}

	public void actionId(){
		BibtexEntry[] bes = panel.mainTable.getSelectedEntries();
		for(BibtexEntry b:bes)
			getId(b);
	}

	private void getInfo(BibtexEntry b){
		String key;
		if(b!=null) {
			key=b.getField("eprint");
			key = (String)JOptionPane.showInputDialog(frame,
					"author: "+b.getField("author")+"\n"
					+"title: "+b.getField("title")+"\n"+
					"Please specify the ArXiv id:", "Get Info",
					JOptionPane.PLAIN_MESSAGE,null,null,key);
		} else {
			key = (String)JOptionPane.showInputDialog(frame,
					"Please specify the ArXiv id:", "New Entry",
					JOptionPane.PLAIN_MESSAGE,null,null,"");

		}
		if(key==null || key.isEmpty()) {
			return;
		}
		key=key.trim();
		OAI2Fetcher f = new OAI2Fetcher();
		//		f.status=frame;
		BibtexEntry e = f.importOai2Entry(key);
		if(e==null || e.getField("author")==null) {
			frame.showMessage("Key: "+key+"not found");
			return;
		}
		if(b==null)
			b=panel.newEntry(BibtexEntryType.MISC);
		String[] fields={"author","title","eprint"};
		for(String s:fields)
			b.setField(s,e.getField(s));
		b.setField("year",getYearFromId(key));
		panel.markBaseChanged();
		//		panel.highlightEntry(b);
		panel.showEntry(b);
	}

	private String getYearFromId(String key){
		// Id has the form math/1109267 or 1109.0267 (September 2011)
		int n=key.lastIndexOf("/");
		String y=key.substring(n+1,n+3);
		if(Integer.parseInt(y)>90)
			return "19" + y;
		else
			return "20" + y;
	}

	private void getId(BibtexEntry b){
		String key=b.getField("eprint");
		key = (String)JOptionPane.showInputDialog(frame,
				"author: "+b.getField("author")+"\n"
				+"title: "+b.getField("title")+"\n"+
				"Please specify the ArXiv id:", "Set id",
				JOptionPane.PLAIN_MESSAGE,null,null,key);
		if(key==null || key.isEmpty())
			return;
		b.setField("eprint",key);
		panel.markBaseChanged();
	}

}
