package bb.chat.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by BB20101997 on 29.11.2014.
 */
public class ConnectDialog extends JDialog implements ActionListener {

	final ClientGUI  cg;
	final JTextField jTextFieldA;
	final JTextField jTextFieldB;

	public ConnectDialog(ClientGUI cg, String t) {
		super(cg, t, true);

		this.cg = cg;

		Box a = Box.createHorizontalBox();
		Box b = Box.createVerticalBox();
		Box c = Box.createVerticalBox();
		Box d = Box.createVerticalBox();

		a.add(Box.createHorizontalGlue());
		a.add(b);
		a.add(c);
		a.add(Box.createGlue());

		JLabel jLabelA = new JLabel("IP :");
		JLabel jLabelB = new JLabel("Port:");

		b.add(jLabelA);
		b.add(jLabelB);

		jTextFieldA = new JTextField("localhost");
		jTextFieldB = new JTextField("256");

		c.add(jTextFieldA);
		c.add(jTextFieldB);

		JButton ok = new JButton("OK");
		ok.addActionListener(this);

		d.add(a);
		d.add(ok);
		add(d);

		pack();
		setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(cg != null) {
			cg.connectTo(jTextFieldA.getText(),Integer.valueOf(jTextFieldB.getText()));
			setVisible(false);
		}
	}

}
