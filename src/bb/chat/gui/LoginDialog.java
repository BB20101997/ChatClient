package bb.chat.gui;

import bb.chat.interfaces.IConnectionHandler;
import bb.chat.network.packet.Handshake.LoginPacket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by BB20101997 on 01.02.2015.
 */
public class LoginDialog extends JDialog implements ActionListener {

	final ClientGUI  cg;
	final IConnectionHandler iConnectionHandler;
	final JTextField jTextFieldA;
	final JPasswordField passwordField;

	public LoginDialog(ClientGUI cg,IConnectionHandler ich, String t) {
		super(cg, t, true);

		this.cg = cg;
		iConnectionHandler = ich;

		Box a = Box.createHorizontalBox();
		Box b = Box.createVerticalBox();
		Box c = Box.createVerticalBox();
		Box d = Box.createVerticalBox();

		a.add(Box.createHorizontalGlue());
		a.add(b);
		a.add(c);
		a.add(Box.createGlue());

		JLabel jLabelA = new JLabel("Username :");
		JLabel jLabelB = new JLabel("Password:");

		b.add(jLabelA);
		b.add(jLabelB);

		jTextFieldA = new JTextField("Your Username");
		passwordField = new JPasswordField("Your Password");

		c.add(jTextFieldA);
		c.add(passwordField);

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
			LoginPacket lp = new LoginPacket();

			char[] pass= passwordField.getPassword();

			lp.setPassword(new String(pass));
			for(int i = 0;i<pass.length;i++){
				pass[i] = 0;
			}
			lp.setUsername(jTextFieldA.getText());

			iConnectionHandler.sendPackage(lp, IConnectionHandler.SERVER);
			setVisible(false);
		}
	}

}
