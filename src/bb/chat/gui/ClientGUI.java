package bb.chat.gui;

import bb.chat.network.ClientMessageHandler;
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

	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			for(BasicChatPanel bcp : BCPList) {
				bcp.IMH.disconnect(null);
			}
			super.windowClosing(e);
		}
	}

	private final List<JMenuItem>      serverList  = new ArrayList<>();
	private final List<BasicChatPanel> BCPList     = new ArrayList<>();
	private final JPanel               jP          = new JPanel();
	private       int                  selectedBCP = -1;

	private final JMenuBar      jMenuBar   = new JMenuBar();
	private final JMenuItem[][] jMenuItems = {
			{new JMenu("Connection"), new JMenuItem("Connect"), new JMenuItem("Disconnect")},
			{new JMenu("Settings"), new JMenuItem("Window Style")},
			{new JMenuItem("Help")},
	};

	private final HashMap<String, Action> actionMap = new HashMap<>();

	public ClientGUI() {

		setJMenuBar(jMenuBar);
		populateMenuBar();
		populateActionMap();
		setLayout(new BorderLayout());
		jP.setLayout(new BoxLayout(jP,BoxLayout.Y_AXIS));
		add(jP, BorderLayout.CENTER);
		addWindowListener(new WindowListen());
		pack();
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	private void populateMenuBar() {
		for(JMenuItem[] jMenuItem : jMenuItems) {
			jMenuBar.add(jMenuItem[0]);
			jMenuItem[0].addActionListener(this);
			if(jMenuItem[0] instanceof JMenu) {
				for(int ii = 1; ii < jMenuItem.length; ii++) {
					jMenuItem[0].add(jMenuItem[ii]);
					jMenuItem[ii].addActionListener(this);
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

		actionMap.put("Disconnect", new Action() {
			@Override
			public void action() {
				synchronized((Integer) selectedBCP) {
					if(selectedBCP != -1) {
						BCPList.get(selectedBCP).IMH.disconnect(null);
						BCPList.remove(selectedBCP);
						selectedBCP = -1;
						invalidate();
					}
				}
				{
					for(BasicChatPanel bcp:BCPList){
						bcp.IMH.shutdown();
					}
					BCPList.clear();
					jP.removeAll();
					revalidate();
					repaint();
				}
			}
		});

		actionMap.put("Window Style", new Action() {
			@Override
			public void action() {
				System.out.println("Creating ChangeDialog");
				ChangeDialog CD = new ChangeDialog(ClientGUI.this, "Change the Look and Feel!");
				CD.setVisible(true);
			}
		});
	}

	public void connectTo(String host, int port) {
		ClientMessageHandler cmh = new ClientMessageHandler();
		BasicChatPanel bcp = new BasicChatPanel(cmh);
		cmh.setBasicChatPanel(bcp);

		if(cmh.connect(host, port)) {
			for(BasicChatPanel basicChatPanel:BCPList){
				basicChatPanel.IMH.shutdown();
			}
			BCPList.clear();
			BCPList.add(bcp);
			jP.removeAll();
			jP.add(bcp);
		} else {
			cmh.shutdown();
		}

		revalidate();
		repaint();
	}

	private abstract class Action {

		public abstract void action();

	}

}
