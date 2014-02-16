package renamefile;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.sf.jabref.*;

@SuppressWarnings("serial")
class RenameFilePanel extends SidePaneComponent implements ActionListener {

	private JButton btnMove,btnCopy,btnDelete,btnSettings,btnHelp;
	private JabRefFrame frame;

	public RenameFilePanel(SidePaneManager manager,JabRefFrame frame,JMenuItem menu) {
		super(manager, GUIGlobals.getIconUrl("openUrl"), "Rename file");
		this.manager = manager;
		this.frame = frame;
		setActiveBasePanel(frame.basePanel());

		btnMove=getButton("Rename","Rename/move file(s).","");
		btnCopy=getButton("Copy","Copy file(s).","");
		btnDelete=getButton("Delete","Detach the local pdf from the " +
				"BibTeX entry and delete the local pdf from the filesystem.","");
		btnSettings=getButton("","Settings","preferences");
		btnHelp=getButton("","Help","help");
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JPanel p1 = new JPanel(new GridBagLayout());
		JPanel p2 = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = .5;
		p1.add(btnMove,c);
		c.gridx = 1;
		p1.add(btnDelete,c);
		c.gridx=0;
		p2.add(btnCopy,c);
		c.gridx=1;
		p2.add(btnSettings,c);
		c.gridx=2;
		p2.add(btnHelp,c);
		p.add(p1);
		p.add(p2);
		setContent(p);
		setName("renamefile");
	}

	private JButton getButton(String title,String tip,String icon){
		JButton btn=new JButton(GUIGlobals.getImage(icon));
		btn.addActionListener(this);
		btn.setText(title);
		btn.setToolTipText(tip);
		return btn;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSettings) {
			new OptionsDialog(frame);
		} else 	if (e.getSource() == btnMove) {
			BibtexEntry[] bes = panel.mainTable.getSelectedEntries();
			new RenameDialog(frame,bes);
		} else 	if (e.getSource() == btnCopy) {
			BibtexEntry[] bes = panel.mainTable.getSelectedEntries();
			new RenameDialog(frame,bes,1);
		} else 	if (e.getSource() == btnDelete) {
			BibtexEntry[] bes = panel.mainTable.getSelectedEntries();
			new RenameDialog(frame,bes,2);
		} else if (e.getSource() == btnHelp){
			Utils.displayAbout(frame);
		}
	}

	public void componentOpening() {
		Globals.prefs.putBoolean("renamefileShow",true);
	}

	public void componentClosing() {
		Globals.prefs.putBoolean("renamefileShow",false);
	}

}



