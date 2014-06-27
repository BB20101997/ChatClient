package bb.chat.client;

import bb.chat.gui.BasicChatPanel;
import bb.chat.gui.ClientGUI;
import bb.chat.network.ClientMessageHandler;
import bb.chat.network.IOHandler;

/**
 * @author BB20101997
 */
public class Main
{

	/**
	 * @param args
	 *            just the usual tArgs to start up the Programm
	 */
	public static void main(String[] args)
	{

		ClientGUI CG = null;
		boolean gui = true;

		for(String s : args)
		{
			if(s.equals("nogui"))
			{
				gui = false;
			}
		}

		ClientMessageHandler CMH = new ClientMessageHandler();

		if(gui)
		{
			CG = new ClientGUI();
			BasicChatPanel BCP = CG.getBCP();
			BCP.addMessageHandler(CMH);
			CMH.addBasicChatPanel(BCP);
		}

		IOHandler IRConsole = new IOHandler(System.in, System.out, CMH);
		IRConsole.setActorName("Client-Console");

	}

}
