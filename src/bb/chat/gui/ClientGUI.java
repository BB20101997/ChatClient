package bb.chat.gui;

import bb.chat.chat.BasicChat;
import bb.chat.client.ClientChat;
import bb.chat.command.BasicCommandRegistry;
import bb.chat.security.BasicPermissionRegistrie;
import bb.chat.security.BasicUserDatabase;
import bb.net.enums.Side;
import bb.net.handler.BasicConnectionManager;
import bb.util.gui.ChangeDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author BB20101997
 */
public class ClientGUI extends JFrame implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if(actionMap.containsKey(e.getActionCommand())) {
			actionMap.get(e.getActionCommand()).action();
		}
	}

	public BasicChat getSelectedBC() {
		return BCList.get(selectedBC);
	}

	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			BCList.forEach(bb.chat.chat.BasicChat::shutdown);
			super.windowClosing(e);
		}
	}

	//private final List<JMenuItem> serverList = new ArrayList<>();
	private final List<BasicChat> BCList     = new ArrayList<>();
	private final JPanel          jP         = new JPanel();
	private       int             selectedBC = -1;

	private final JMenuBar      jMenuBar   = new JMenuBar();
	private final JMenuItem[][] jMenuItems = {
			{new JMenu("Connection"), new JMenuItem("Connect"), new JMenuItem("Login"), new JMenuItem("Disconnect")},
			{new JMenu("Settings"), new JMenuItem("Window Style")},
			{new JMenuItem("Help")},
	};

	private final HashMap<String, Action> actionMap = new HashMap<>();

	public ClientGUI() {

		setJMenuBar(jMenuBar);
		populateMenuBar();
		populateActionMap();
		setLayout(new BorderLayout());
		jP.setLayout(new BoxLayout(jP, BoxLayout.Y_AXIS));
		add(jP, BorderLayout.CENTER);
		addWindowListener(new WindowListen());
		setMinimumSize(new Dimension(500, 250));
		pack();
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	private void populateMenuBar() {
		for(JMenuItem[] jMenuItem : jMenuItems) {
			jMenuBar.add(jMenuItem[0]);
			jMenuItem[0].addActionListener(this);
			if(jMenuItem[0] instanceof JMenu) {
				for(int i = 1; i < jMenuItem.length; i++) {
					jMenuItem[0].add(jMenuItem[i]);
					jMenuItem[i].addActionListener(this);
				}
			}
		}
	}

	private void populateActionMap() {
		actionMap.put("Connect", new Action() {
			@Override
			public void action() {
				ConnectDialog cd = new ConnectDialog(ClientGUI.this, "Connect to ...");
				cd.setVisible(true);
			}
		});

		actionMap.put("Login", new Action() {
			@Override
			public void action() {
				LoginDialog ld = new LoginDialog(ClientGUI.this, ClientGUI.this.getSelectedBC(), "LoginDialog");
				ld.setVisible(true);
			}
		});

		actionMap.put("Disconnect", new Action() {
			@Override
			public void action() {
				synchronized((Integer) selectedBC) {
					if(selectedBC != -1) {
						BCList.get(selectedBC).shutdown();
						BCList.remove(selectedBC);
						selectedBC = -1;
						invalidate();
					}
				}
				{
					for(BasicChat bcp:BCList){
						bcp.shutdown();
					}
					BCList.clear();
					jP.removeAll();
					revalidate();
					repaint();
				}
			}
		});

		actionMap.put("Window Style", new Action() {
			@Override
			public void action() {
				ChangeDialog CD = new ChangeDialog(ClientGUI.this, "Change the Look and Feel!");
				CD.setVisible(true);
			}
		});
	}

	public void connectTo(String host, int port) {
		BasicChat bc = new ClientChat(new BasicConnectionManager(Side.CLIENT,port), new BasicPermissionRegistrie(), new BasicUserDatabase(), new BasicCommandRegistry());
		BasicChatPanel bcp = new BasicChatPanel(bc);
		bc.setBasicChatPanel(bcp);

		if(bc.getIConnectionManager().connect(host, port)) {
			for(BasicChat basicChat:BCList){
				basicChat.shutdown();
			}
			BCList.clear();
			BCList.add(bc);
			jP.removeAll();
			jP.add(bcp);
			selectedBC = 0;
		} else {
			bc.shutdown();
		}

		revalidate();
		repaint();
	}

	private abstract class Action {

		public abstract void action();

	}

}
