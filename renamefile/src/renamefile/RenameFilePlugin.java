package renamefile;


import net.sf.jabref.*;
import net.sf.jabref.plugin.*;

import javax.swing.*;

import java.awt.event.*;


public class RenameFilePlugin implements SidePanePlugin, ActionListener {

	protected SidePaneManager manager;
	private JMenuItem toggleMenu;
	private JabRefFrame frame;
	private RenameFilePanel comp = null;

	public void init(JabRefFrame frame, SidePaneManager manager) {
		this.manager = manager;
		this.frame = frame;

		toggleMenu = new JMenuItem("Toggle rename file panel",new ImageIcon(GUIGlobals.getIconUrl("openUrl")));
//		toggleMenu.setMnemonic(KeyEvent.VK_R);
		toggleMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
		toggleMenu.addActionListener(this);

		Globals.prefs.defaults.put("renamefileShow",true);
		Globals.prefs.putDefaultValue("SameFolder",true);//move to the same folder, the new path is relative/absolute depending on the old path
		Globals.prefs.putDefaultValue("SameName",false);
		Globals.prefs.putDefaultValue("MoveFolder",""); // relative/absolute folder
		Globals.prefs.putDefaultValue("RenamePattern","[auth:lower]_[veryshorttitle:lower]_[year]");
		Utils.init(frame);
	}

	public SidePaneComponent getSidePaneComponent() {
		if(comp==null)
			comp = new RenameFilePanel(manager,frame,toggleMenu);
		return comp;
	}

	public JMenuItem getMenuItem() {
		if (Globals.prefs.getBoolean("renamefileShow"))
			manager.show("renamefile");
		return toggleMenu;
	}

	public String getShortcutKey() {
		return null;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == toggleMenu)
			manager.toggle("renamefile");
	}
}
