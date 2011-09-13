package fetchArxiv;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.sf.jabref.*;

@SuppressWarnings("serial")
class FetchArxivPanel extends SidePaneComponent implements ActionListener {

	private JButton btnNew,btnInfo,btnId;
	private Actions actions;

	public FetchArxivPanel(SidePaneManager manager,JabRefFrame frame,JMenuItem menu) {
		super(manager, GUIGlobals.getIconUrl("openUrl"), "Fetch arXiv");
		this.manager = manager;
		setActiveBasePanel(frame.basePanel());
		actions = new Actions(frame);

		btnNew=getButton("New Entry","Create entry from an arXiv eprint","");
		btnInfo=getButton("Get Info","Fetch entry information from an arXiv eprint","");
		btnId=getButton("Set Id","Set eprint Id","");

		JPanel split = new JPanel();
		split.setLayout(new BoxLayout(split, BoxLayout.LINE_AXIS));
		split.add(btnNew);
		split.add(btnInfo);
		split.add(btnId);

		setContent(split);
		setName("fetchArxiv");
	}

	private JButton getButton(String title,String tip,String icon){
		JButton btn=new JButton(GUIGlobals.getImage(icon));
		btn.addActionListener(this);
		btn.setText(title);
		btn.setToolTipText(tip);
		return btn;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnNew)
			actions.actionNew();
		else if (e.getSource() == btnInfo)
			actions.actionInfo();
		else if (e.getSource() == btnId)
			actions.actionId();
	}

	public void componentOpening() {
		Globals.prefs.putBoolean("FAShow",true);
	}

	public void componentClosing() {
		Globals.prefs.putBoolean("FAShow",false);
	}


}