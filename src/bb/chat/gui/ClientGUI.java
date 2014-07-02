package bb.chat.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * @author BB20101997
 */
@SuppressWarnings("serial")
public class ClientGUI extends JFrame
{

	private class WindowLisen extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{

			super.windowClosing(e);
			System.out.println("Disposing Window");
			dispose();
			System.exit(DISPOSE_ON_CLOSE);
		}
	}

	private final BasicChatPanel	BCP;

	/**
	 * Constructer to setup the JFrame
	 */
	public ClientGUI()
	{

		super("Client GUI");
		BCP = new BasicChatPanel();
		add(BCP);
		BCP.ChatSendBar.setText("/connect localhost");
		setMinimumSize(new Dimension(500, 250));
		pack();
		setVisible(true);
		addWindowListener(new WindowLisen());
	}

	/**
	 * @return the BasicChatPanel the GUI is linked to
	 */
	public BasicChatPanel getBCP()
	{

		return BCP;
	}

}
