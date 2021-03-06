package bb.chat.gui;

import bb.chat.enums.Bundles;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by BB20101997 on 29.11.2014.
 */
@SuppressWarnings("ClassWithoutLogger")
public class ConnectDialog extends JDialog implements ActionListener {

	private final ClientGUI  cg;
	private final JTextField jTextFieldA;
	private final JTextField jTextFieldB;

	@SuppressWarnings("SameParameterValue")
	public ConnectDialog(ClientGUI cg, String t) {
		super(cg, t, true);

		this.cg = cg;

		Box box = Box.createHorizontalBox();
		Box box1 = Box.createVerticalBox();
		Box box2 = Box.createVerticalBox();
		Box box3 = Box.createVerticalBox();

		box.add(Box.createHorizontalGlue());
		box.add(box1);
		box.add(box2);
		box.add(Box.createGlue());

		JLabel jLabelA = new JLabel(Bundles.BUTTON_LABEL.getString("label.ip"));
		JLabel jLabelB = new JLabel(Bundles.BUTTON_LABEL.getString("label.port"));

		box1.add(jLabelA);
		box1.add(jLabelB);

		jTextFieldA = new JTextField(Bundles.BUTTON_LABEL.getString("default_text.host"));
		jTextFieldB = new JTextField(Bundles.BUTTON_LABEL.getString("default_text.port"));

		box2.add(jTextFieldA);
		box2.add(jTextFieldB);

		JButton ok = new JButton(Bundles.BUTTON_LABEL.getString("button.ok"));
		ok.addActionListener(this);

		box3.add(box);
		box3.add(ok);
		add(box3);

		pack();
		setResizable(false);
	}

	@SuppressWarnings("PublicMethodWithoutLogging")
	@Override
	public void actionPerformed(ActionEvent e) {
		if(cg != null) {
			cg.connectTo(jTextFieldA.getText(),Integer.valueOf(jTextFieldB.getText()));
			setVisible(false);
		}
	}

}
