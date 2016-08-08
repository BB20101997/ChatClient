package bb.chat.gui;

import bb.chat.chat.BasicChat;
import bb.chat.client.ClientChat;
import bb.chat.enums.Bundles;
import bb.net.event.ConnectionClosedEvent;
import bb.net.interfaces.IConnectionEvent;
import bb.util.event.EventHandler;
import bb.util.file.log.BBLogHandler;
import bb.util.file.log.Constants;
import bb.util.gui.ChangeDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author BB20101997
 */
public class ClientGUI extends JFrame implements ActionListener {

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger log;

	static {
		log = Logger.getLogger(ClientGUI.class.getName());
		//noinspection DuplicateStringLiteralInspection
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
			log.finest("Shutting down, someone closed the Window!"+System.lineSeparator()+"Suffocation!");
			BCList.forEach(bb.chat.chat.BasicChat::shutdown);
			super.windowClosing(e);
		}
	}

	//private final List<JMenuItem> serverList = new ArrayList<>();
	private final List<BasicChat> BCList     = new ArrayList<>();
	private final JPanel          jP         = new JPanel();
	private       int             selectedBC = -1;

	private final JMenuBar      jMenuBar   = new JMenuBar();
	private  JMenuItem[][] jMenuItems;

	private final HashMap<String, Action> actionMap = new HashMap<>();

	private final  JMenu connection = new JMenu(),settings = new JMenu();
	private final JMenuItem connect = new JMenuItem(),disconnect = new JMenuItem(),login = new JMenuItem(),style = new JMenuItem(),help = new JMenuItem();

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

	public void setLabels(){
		//JMenu
		ResourceBundle ButtonLabels = Bundles.BUTTONLABLE.getResource();
		connection.setText(ButtonLabels.getString("connection"));
		settings.setText(ButtonLabels.getString("settings"));
		//JMenuItem
		connect.setText(ButtonLabels.getString("connect"));
		disconnect.setText(ButtonLabels.getString("disconnect"));
		login.setText(ButtonLabels.getString("login"));
		style.setText(ButtonLabels.getString("style"));
		help.setText(ButtonLabels.getString("help"));
	}

	private void setActionCommands(){
		connect.setActionCommand("Connect");
		disconnect.setActionCommand("Disconnect");
		login.setActionCommand("Login");
		style.setActionCommand("Style");
	}

	private void populateMenuItemArray(){
		jMenuItems = new JMenuItem[][]{
				{connection, connect,login, disconnect},
				{settings, style},
				{help},
		};
	}

	private void populateMenuBar() {
		populateMenuItemArray();
		setLabels();
		setActionCommands();
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

		actionMap.put("Style", () -> new ChangeDialog(ClientGUI.this, "Change the Look and Feel!").setVisible(true));
	}

	public void connectTo(String host, int port) {
		log.finer("Connecting to "+host+" on Port "+port+"!");
		BasicChat bc = new ClientChat();
		BasicChatPanel bcp = new BasicChatPanel(bc);
		bc.setBasicChatPanel(bcp);

		if(bc.getIConnectionManager().connect(host, port)) {
			BCList.forEach(BasicChat::shutdown);
			BCList.clear();
			BCList.add(bc);
			jP.removeAll();
			jP.add(bcp);
			selectedBC = 0;
			bc.getIConnectionManager().addConnectionEventHandler(new ConEventHandler(bcp));
		} else {
			bc.shutdown();
		}

		revalidate();
		repaint();
	}

	private interface Action {
		void action();
	}

	public class ConEventHandler extends EventHandler<IConnectionEvent>{

		final BasicChatPanel basicChatPanel;

		ConEventHandler(BasicChatPanel bcp){
			basicChatPanel = bcp;
		}

		@Override
		public void HandleEvent(IConnectionEvent event) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
			if(event instanceof ConnectionClosedEvent) {
				try {
					super.HandleEvent(event);
				} catch(Exception e) {
					//noinspection StringConcatenationMissingWhitespace
					log.severe("WTF when't wrong here?" + System.lineSeparator() + "I didn't think it was even be possible!");
					throw e;
				}
			}
		}

		@SuppressWarnings("UnusedParameters")
		public void handleEvent(ConnectionClosedEvent cce) {
			basicChatPanel.println(Bundles.MESSAGE.getResource().getString("connection.closedLost"));
		}

	}

}
