package fetchArxiv;


import net.sf.jabref.*;
import net.sf.jabref.plugin.*;

import javax.swing.*;

import java.awt.event.*;


public class FetchArxivPlugin implements SidePanePlugin, ActionListener {

	private JabRefFrame frame;
	protected SidePaneManager manager;
	private JMenuItem toggleMenu;
	private FetchArxivPanel comp;

	public void init(JabRefFrame frame, SidePaneManager manager) {
		this.manager = manager;
		this.frame = frame;
		toggleMenu = new JMenuItem("Toggle fetch arXiv panel",new ImageIcon(GUIGlobals.getIconUrl("openUrl")));
		toggleMenu.setMnemonic(KeyEvent.VK_F);
		toggleMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK+ActionEvent.ALT_MASK));
		toggleMenu.addActionListener(this);
		Globals.prefs.defaults.put("FAShow",true);
	}

	public SidePaneComponent getSidePaneComponent() {
		if (comp == null)
			comp = new FetchArxivPanel(manager,frame,toggleMenu);
		return comp;
	}

	public JMenuItem getMenuItem() {
		if (Globals.prefs.getBoolean("FAShow"))
			manager.show("fetchArxiv");
		return toggleMenu;
	}

	public String getShortcutKey() {
		return null;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == toggleMenu)
			manager.toggle("fetchArxiv");
	}
}
