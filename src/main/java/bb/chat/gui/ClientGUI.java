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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static bb.chat.client.ClientConstants.LOG_NAME;

/**
 * @author BB20101997
 */
public class ClientGUI extends JFrame implements ActionListener {

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger log;

	private static final String CMD_CONNECT = "Connect", CMD_DISCONNECT = "Disconnect", CMD_LOGIN = "Login", CMD_STYLE = "Style";

	static {
		log = Logger.getLogger(ClientGUI.class.getName());
		//noinspection DuplicateStringLiteralInspection
		log.addHandler(new BBLogHandler(Constants.getLogFile(LOG_NAME)));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		log.fine(MessageFormat.format(Bundles.LOG_TEXT.getString("log.chat.action"), e));
		if(actionMap.containsKey(e.getActionCommand())) {
			actionMap.get(e.getActionCommand()).action();
		}
	}

	@SuppressWarnings("WeakerAccess")
	public BasicChat getSelectedBC() {
		log.finest(Bundles.LOG_TEXT.getString("log.gui.return.bc"));
		if(selectedBC < 0) {
			return null;
		}
		return BCList.get(selectedBC);
	}

	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			//noinspection StringConcatenationMissingWhitespace
			log.finest(Bundles.LOG_TEXT.getString("log.gui.close"));
			BCList.forEach(bb.chat.chat.BasicChat::shutdown);
			super.windowClosing(e);
		}
	}

	//private final List<JMenuItem> serverList = new ArrayList<>();
	private final List<BasicChat> BCList     = new ArrayList<>();
	private final JPanel          jP         = new JPanel();
	private       int             selectedBC = -1;

	private final JMenuBar jMenuBar = new JMenuBar();
	private JMenuItem[][] jMenuItems;

	private final HashMap<String, Action> actionMap = new HashMap<>();

	private final JMenu connection = new JMenu(), settings = new JMenu();
	private final JMenuItem connect = new JMenuItem(), disconnect = new JMenuItem(), login = new JMenuItem(), style = new JMenuItem(), help = new JMenuItem();

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

	public void setLabels() {
		log.fine(Bundles.LOG_TEXT.getString("log.labels.set"));
		//JMenu
		ResourceBundle buttonLabels = Bundles.BUTTON_LABEL.getResource();
		connection.setText(buttonLabels.getString("button.connection"));
		settings.setText(buttonLabels.getString("button.settings"));
		//JMenuItem
		connect.setText(buttonLabels.getString("button.connect"));
		disconnect.setText(buttonLabels.getString("button.disconnect"));
		login.setText(buttonLabels.getString("button.login"));
		style.setText(buttonLabels.getString("button.style"));
		help.setText(buttonLabels.getString("button.help"));
	}

	private void setActionCommands() {
		connect.setActionCommand(CMD_CONNECT);
		disconnect.setActionCommand(CMD_DISCONNECT);
		login.setActionCommand(CMD_LOGIN);
		style.setActionCommand(CMD_STYLE);
	}

	private void populateMenuItemArray() {
		jMenuItems = new JMenuItem[][]{{connection, connect, login, disconnect}, {settings, style}, {help},};
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
		actionMap.put(CMD_CONNECT, () -> new ConnectDialog(ClientGUI.this, Bundles.BUTTON_LABEL.getString("title.connect")).setVisible(true));

		actionMap.put(CMD_LOGIN, () -> new LoginDialog(ClientGUI.this, ClientGUI.this.getSelectedBC(), Bundles.BUTTON_LABEL.getString("title.login")).setVisible(true));

		actionMap.put(CMD_DISCONNECT, () -> {
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

		actionMap.put(CMD_STYLE, () -> new ChangeDialog(ClientGUI.this, Bundles.BUTTON_LABEL.getString("title.style")).setVisible(true));
	}

	public void connectTo(final String host,final int port) {
		log.finer(MessageFormat.format(Bundles.LOG_TEXT.getString("log.connect.hostport"),host,port));
		BasicChat      bc  = new ClientChat();
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

	@SuppressWarnings("InterfaceNamingConvention")
	private interface Action {
		void action();
	}

	public static class ConEventHandler extends EventHandler<IConnectionEvent> {

		final BasicChatPanel basicChatPanel;

		ConEventHandler(BasicChatPanel bcp) {
			basicChatPanel = bcp;
		}

		@Override
		public void HandleEvent(IConnectionEvent event) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
			if(event instanceof ConnectionClosedEvent) {
				try {
					super.HandleEvent(event);
				}
				catch(NoSuchMethodException e) {
					//noinspection StringConcatenationMissingWhitespace
					log.severe(Bundles.LOG_TEXT.getString("log.exception.no_such_method"));
					throw e;
				}
				catch(IllegalAccessException e) {
					//noinspection StringConcatenationMissingWhitespace
					log.severe(Bundles.LOG_TEXT.getString("log.exception.illegal_access"));
					throw e;
				}
				catch(InvocationTargetException e) {
					//noinspection StringConcatenationMissingWhitespace
					log.severe(Bundles.LOG_TEXT.getString("log.exception.invocation_target"));
					throw e;
				}
			}
		}

		@SuppressWarnings({"UnusedParameters", "MethodNamesDifferingOnlyByCase"})
		public void handleEvent(ConnectionClosedEvent cce) {
			log.fine(Bundles.LOG_TEXT.getString("log.connection.closedLost"));
			basicChatPanel.println(Bundles.MESSAGE.getResource().getString("connection.closedLost"));
		}

	}

}
