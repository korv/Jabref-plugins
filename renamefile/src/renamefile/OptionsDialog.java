package renamefile;




import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.dyno.visual.swing.layouts.*;
import org.dyno.visual.swing.layouts.GroupLayout;

import net.sf.jabref.Globals;

public class OptionsDialog extends JDialog implements ActionListener{
	private JCheckBox cbName,cbDir;
	private JTextField tfDir,tfPattern;
	private JButton btnOk,btnCancel;

	public void mess(String s){
		JOptionPane.showMessageDialog (this,s, "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	public OptionsDialog(Frame parent) {
		super(parent);
		initComponents();
	}


	private void initComponents() {
		initElements();
		readValues();
		JPanel p,p1;

		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		add(panel);
		
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

		p=new JPanel();
		p.add(btnOk);
		p.add(btnCancel);
		p1=new JPanel();
		p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
		p1.add(p);
		panel.add(p1);

		setSize(800, 120);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Options");
		setLocationRelativeTo(null);
		getContentPane().setPreferredSize(getSize());
		pack();
		setVisible(true);
	}

	private void initElements(){
		cbDir = new JCheckBox();
		cbDir.setText("Use folder:");
		cbName = new JCheckBox();
		cbName.setText("Use name pattern:");
		tfDir = new JTextField();
		tfDir.addActionListener(this);
		tfPattern = new JTextField();
		btnOk = new JButton();
		btnOk.setText("Ok");
		btnOk.addActionListener(this);
		btnOk.setPreferredSize(new Dimension(100, 26));
		btnCancel = new JButton();
		btnCancel.setText("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.setPreferredSize(new Dimension(100, 26));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o=e.getSource();
		if(o==btnOk) {
			saveValues();
			dispose();
		} else if(o==btnCancel)
			dispose();
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

}
