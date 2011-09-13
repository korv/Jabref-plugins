package renamefile;


import javax.swing.*;
import javax.swing.table.*;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import net.sf.jabref.*;
import net.sf.jabref.gui.*;

public class RenameDialog extends JDialog implements ActionListener, FocusListener{
	private static final long serialVersionUID = 1L;
	private final int[] renWidth={10,100,100,300,300,300};
	private final int[] delWidth={10,100,100,300,300};
	private JabRefFrame frame;
	private BibtexEntry[] bes;
	private int mode;


	private FLTableModel tm;
	private JTable table;
	private JPanel panel;
	private JCheckBox cbName,cbDir;
	private JTextField tfDir,tfPattern;
	private JButton btnOk,btnCancel;

	private final String[] titleString={"Rename/move files","Copy files","Delete files"};

	public RenameDialog(JabRefFrame frame,BibtexEntry[] bes ) {
		this(frame,bes,0);
	}

	public RenameDialog(JabRefFrame frame,BibtexEntry[] bes, int mode) {
		super(frame);
		this.frame=frame;
		this.bes=bes;
		this.mode=mode;
		initElements();

		JPanel p,p1;
		panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		add(panel);

		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.add(new JScrollPane(table));
		p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panel.add(p);

		if(mode!=2){
			p=new JPanel();
			p.setLayout(new GroupLayout());
			p.add(cbDir, new Constraints(new Leading(10, 150, 8, 8), new Leading(10, 10, 10)));
			p.add(cbName, new Constraints(new Leading(10, 150, 8, 8), new Leading(38, 8, 8)));
			p.add(tfDir, new Constraints(new Bilateral(170, 12, 4), new Leading(12, 12, 12)));
			p.add(tfPattern, new Constraints(new Bilateral(170, 12, 4), new Leading(40, 12, 12)));

			p1=new JPanel();
			p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
			p1.add(p);
			panel.add(p1);
		}

		p=new JPanel();
		p.add(btnOk);
		p.add(btnCancel);
		p1=new JPanel();
		p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
		p1.add(p);
		panel.add(p1);

		setTitle(titleString[mode]);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getRootPane().setDefaultButton(btnOk);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initElements(){
		if(mode!=2){
			cbDir = new JCheckBox();
			cbDir.setText("Use folder:");
			cbDir.addActionListener(this);
			cbName = new JCheckBox();
			cbName.setText("Use name pattern:");
			cbName.addActionListener(this);
			tfDir = new JTextField();
			tfDir.addFocusListener(this);
			tfDir.addActionListener(this);
			tfPattern = new JTextField();
			tfPattern.addFocusListener(this);
			tfPattern.addActionListener(this);
			readValues();
		}
		btnOk = new JButton();
		btnOk.setText("Ok");
		btnOk.addActionListener(this);
		btnOk.setPreferredSize(new Dimension(100, 26));
		btnCancel = new JButton();
		btnCancel.setText("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.setPreferredSize(new Dimension(100, 26));
		tm = new FLTableModel(bes,mode);
		table = new JTable(tm);
		table.setPreferredScrollableViewportSize(new Dimension(800, 200));
		table.setFillsViewportHeight(true);
		//	table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		int[] width = mode==2 ? delWidth : renWidth;
		for(int i=0;i<width.length;i++)
			table.getColumnModel().getColumn(i).setPreferredWidth(width[i]);
	}


	class FLTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private final String[] renNames = {"","Key","Author", "Title", "File","New File"};
		private final String[] delNames = {"","Key","Author", "Title", "File"};
		private String[] columnNames;
		public ArrayList<HashMap<Object,Object>> data;
		private int mode;

		public FLTableModel(BibtexEntry[] b,int mode) {
			this.mode=mode;
			columnNames = mode==2 ? delNames : renNames;
			setData(b);
		}

		public void setData(BibtexEntry[] b){
			data = new ArrayList<HashMap<Object,Object>>();			
			FileListTableModel m;
			for (BibtexEntry e:b) {
				m = new FileListTableModel();
				m.setContent(e.getField("file"));
				for (int j=0;j<m.getRowCount();j++) {
					FileListEntry f = m.getEntry(j);
					HashMap<Object,Object> r= new HashMap<Object,Object>();
					r.put(0,new Boolean(true));
					r.put(1,tostr(e.getField(BibtexFields.KEY_FIELD)));
					r.put(2,tostr(e.getField("author")));
					r.put(3,tostr(e.getField("title")));
					r.put(4,f.getLink());
					r.put("fe",f); //FileListEntry
					r.put("be",e); //BibtexEntry
					if(mode!=2)
						r.put(5,getNewFileName(f.getLink(),e));
					data.add(r);
				}
			}
		}
		private Object tostr(Object s){
			return s==null ? "" : s;
		}

		public void changeData(BibtexEntry[] b){
			setData(b);
			fireTableDataChanged();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data.get(row).get(col);
		}

		public Class<? extends Object> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			if (col < 1) {
				return true;
			} else {
				return false;
			}
		}

		public void setValueAt(Object value, int row, int col) {
			data.get(row).put(col,value);
			fireTableCellUpdated(row, col);
		}

		public HashMap<Object,Object> getRow(int row){
			return data.get(row);
		}
	}

	private void readValues(){
		cbDir.setSelected(!Globals.prefs.getBoolean("SameFolder"));
		cbName.setSelected(!Globals.prefs.getBoolean("SameName"));
		tfDir.setText(Globals.prefs.get("MoveFolder"));
		tfPattern.setText(Globals.prefs.get("RenamePattern"));
	}
	private void saveValues(){
		Globals.prefs.putBoolean("SameFolder",!cbDir.isSelected());
		Globals.prefs.putBoolean("SameName",!cbName.isSelected());
		Globals.prefs.put("MoveFolder",tfDir.getText());
		Globals.prefs.put("RenamePattern",tfPattern.getText());
	}

	public void actionPerformed(ActionEvent evt) {
		Object s=evt.getSource();
		if (s == btnOk) {
			if(mode!=2)
				saveValues();
			for (int i = 0;i < tm.getRowCount();i++) {
				HashMap<Object,Object> r=tm.getRow(i);
				Boolean sel = (Boolean)r.get(0);
				if (sel) {
					BibtexEntry b=(BibtexEntry)r.get("be");
					String of = ((FileListEntry)r.get("fe")).getLink();
					switch(mode){
					case 0: renameFile(b,of,this); break;
					case 1: copyFile(b,of,this); break;
					case 2: Utils.removeFile(b,of); break;
					}
				}
			}
			frame.basePanel().markBaseChanged();
			dispose();
		} else if (s == btnCancel) {
			dispose();
		} else if (mode!=2 && (s==cbDir || s==cbName || s==tfDir || s==tfPattern))
			tm.changeData(bes);
	}

	public void focusLost(FocusEvent e) {
		tm.changeData(bes);
	}
	public String getNewFileName(String ofn,BibtexEntry b){
		boolean sameDir=!cbDir.isSelected();
		boolean sameName=!cbName.isSelected();
		String dir=tfDir.getText();
		String pattern=tfPattern.getText();
		return Utils.getNewFileName(ofn, b,sameDir, sameName, dir, pattern);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void renameFile(BibtexEntry b,String ofn, Component comp){
		String dir = Utils.getFileDir();
		File of = Util.expandFilename(ofn, dir); //null if file does not exist
		if (of != null && of.exists()) {
			String fn= Utils.getNewFileName(ofn,b);
			while (true) {
				File file = new File(fn);
				if(!file.isAbsolute())
					file=new File(dir,fn);
				try {
					if(file.getCanonicalPath().equals(of.getCanonicalPath())){
						Utils.removeFileEntry(b,ofn);
						Utils.addFileEntry(b,fn);
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (file.exists()) {
					Object[] options = {"Overwrite file.", "Use a different filename.", "Cancel rename."};
					int n = JOptionPane.showOptionDialog(comp,"A file named '" + fn + "' already exists.  What shall I do?",
							"File already exists",
							JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,	null,options,options[1]);
					if (n != 0 && n!=1)
						return;
					if (n == 1) {
						String k = (String)JOptionPane.showInputDialog(comp,"Please specify the filename to be used.",
								"New filename",
								JOptionPane.PLAIN_MESSAGE,null,null,fn);
						if (k == null)
							return;
						fn = k;
						continue;
					} else
						file.delete();
				}
				File par=file.getParentFile();
				if(par!=null)
					par.mkdirs();
				if (!of.renameTo(file)) {
					String k = (String)JOptionPane.showInputDialog(comp,"The filename " + fn + 
							" seems to be invalid on your filesystem.\n" +
							"Please specify a different filename to be used.",
							"New filename",
							JOptionPane.PLAIN_MESSAGE,null,null,fn);
					if (k == null)
						return;
					fn = k;
					continue;
				}
				Utils.removeFileEntry(b,ofn);
				Utils.addFileEntry(b,fn);
				break;
			}
		}	    
	}

	public void copyFile(BibtexEntry b,String ofn, Component comp){
		String dir = Utils.getFileDir();
		File of = Util.expandFilename(ofn, dir); //null if file does not exist
		if (of != null && of.exists()) {
			String fn= Utils.getNewFileName(ofn,b);
			while (true) {
				File file = new File(fn);
				if(!file.isAbsolute())
					file=new File(dir,fn);
				try {
					if(file.getCanonicalPath().equals(of.getCanonicalPath())){
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (file.exists()) {
					Object[] options = {"Overwrite file.", "Use a different filename.", "Cancel copy file."};
					int n = JOptionPane.showOptionDialog(comp,"A file named '" + fn + "' already exists.  What shall I do?",
							"File already exists",
							JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,	null,options,options[1]);
					if (n != 0 && n!=1)
						return;
					if (n == 1) {
						String k = (String)JOptionPane.showInputDialog(comp,"Please specify the filename to be used.",
								"New filename",
								JOptionPane.PLAIN_MESSAGE,null,null,fn);
						if (k == null)
							return;
						fn = k;
						continue;
					} else
						file.delete();
				}
				File par=file.getParentFile();
				if(par!=null)
					par.mkdirs();
				if (!Utils.copyFile(of,file)) {
					String k = (String)JOptionPane.showInputDialog(comp,"The filename " + fn + 
							" seems to be invalid on your filesystem.\n" +
							"Please specify a different filename to be used.",
							"New filename",
							JOptionPane.PLAIN_MESSAGE,null,null,fn);
					if (k == null)
						return;
					fn = k;
					continue;
				}
				break;
			}
		}	    
	}

}
