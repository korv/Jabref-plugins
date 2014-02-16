package renamefile;


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

		btnMove=getButton("Rename","Rename/move file(s).","redo");
		btnCopy=getButton("Copy","Copy file(s).","save");
		btnDelete=getButton("Delete","Detach the local pdf from the " +
				"BibTeX entry and delete the local pdf from the filesystem.","delete");
		btnSettings=getButton("","Settings","preferences");
		btnHelp=getButton("","Help","help");

		JPanel split = new JPanel();
		split.setLayout(new BoxLayout(split, BoxLayout.LINE_AXIS));
		split.add(btnMove);
		split.add(btnCopy);
		split.add(btnDelete);
		split.add(btnSettings);
		split.add(btnHelp);

		setContent(split);
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



