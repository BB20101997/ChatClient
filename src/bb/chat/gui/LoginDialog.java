package bb.chat.gui;

import bb.chat.interfaces.IChat;
import bb.chat.network.packet.handshake.LoginPacket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by BB20101997 on 01.02.2015.
 */
public class LoginDialog extends JDialog implements ActionListener {

	final ClientGUI  cg;
	final IChat iChat;
	final JTextField jTextFieldA;
	final JPasswordField passwordField;

	public LoginDialog(ClientGUI cg,IChat ich, @SuppressWarnings("SameParameterValue") String t) {
		super(cg, t, true);

		this.cg = cg;
		iChat = ich;

		Box a = Box.createHorizontalBox();
		Box box = Box.createVerticalBox();
		Box box1 = Box.createVerticalBox();
		Box box2 = Box.createVerticalBox();

		a.add(Box.createHorizontalGlue());
		a.add(box);
		a.add(box1);
		a.add(Box.createGlue());

		JLabel jLabelA = new JLabel("Username :");
		JLabel jLabelB = new JLabel("Password:");

		box.add(jLabelA);
		box.add(jLabelB);

		jTextFieldA = new JTextField("Your Username");
		passwordField = new JPasswordField("Your Password");

		box1.add(jTextFieldA);
		box1.add(passwordField);

		JButton ok = new JButton("OK");
		ok.addActionListener(this);

		box2.add(a);
		box2.add(ok);
		add(box2);

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

			iChat.getIConnectionManager().sendPackage(lp,iChat.getIConnectionManager().SERVER());
			setVisible(false);
		}
	}

}
