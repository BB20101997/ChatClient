package bb.chat.gui;

import bb.chat.chat.BasicChat;
import bb.chat.client.ClientChat;
import bb.chat.command.BasicCommandRegistry;
import bb.chat.security.BasicPermissionRegistrie;
import bb.chat.security.BasicUserDatabase;
import bb.net.enums.Side;
import bb.net.handler.BasicConnectionManager;
import bb.util.file.log.BBLogHandler;
import bb.util.file.log.Constants;
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
import java.util.logging.Logger;

/**
 * @author BB20101997
 */
public class ClientGUI extends JFrame implements ActionListener {

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger log;

	static {
		log = Logger.getLogger(ClientGUI.class.getName());
		log.addHandler(new BBLogHandler(Constants.getLogFile("ChatClient")));
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		log.fine("An Action Accured reacting based on actioMap!");
		if(actionMap.containsKey(e.getActionCommand())) {
			actionMap.get(e.getActionCommand()).action();
		}
	}

	@SuppressWarnings("WeakerAccess")
	public BasicChat getSelectedBC() {
		log.finest("Getting SelectedBC");
		if(selectedBC<0){
			return null;
		}
		return BCList.get(selectedBC);
	}

	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			//noinspection StringConcatenationMissingWhitespace
			log.finest("Shutting down, someone cload the Window!"+System.lineSeparator()+"Suffocation!");
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
		actionMap.put("Connect", () -> new ConnectDialog(ClientGUI.this, "Connect to ...").setVisible(true));

		actionMap.put("Login", () -> new LoginDialog(ClientGUI.this, ClientGUI.this.getSelectedBC(), "LoginDialog").setVisible(true));

		actionMap.put("Disconnect", () -> {
			synchronized((Integer) selectedBC) {
				if(selectedBC != -1) {
					BCList.get(selectedBC).shutdown();
					BCList.remove(selectedBC);
					selectedBC = -1;
					invalidate();
				}
			}
			{
				BCList.forEach(BasicChat::shutdown);
				BCList.clear();
				jP.removeAll();
				revalidate();
				repaint();
			}
		});

		actionMap.put("Window Style", () -> new ChangeDialog(ClientGUI.this, "Change the Look and Feel!").setVisible(true));
	}

	public void connectTo(String host, int port) {
		log.finer("Connecting to "+host+" on Port "+port+"!");
		BasicChat bc = new ClientChat(new BasicConnectionManager(Side.CLIENT,port), new BasicPermissionRegistrie(), new BasicUserDatabase(), new BasicCommandRegistry());
		BasicChatPanel bcp = new BasicChatPanel(bc);
		bc.setBasicChatPanel(bcp);

		if(bc.getIConnectionManager().connect(host, port)) {
			BCList.forEach(BasicChat::shutdown);
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

	private interface Action {
		void action();
	}

}
