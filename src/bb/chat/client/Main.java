package bb.chat.client;

import bb.chat.gui.BasicChatPanel;
import bb.chat.gui.ClientGUI;
import bb.chat.network.ClientMessageHandler;

import java.io.IOException;

/**
 * @author BB20101997
 */
class Main
{

	/**
	 * @param args
	 *            just the usual tArgs to start up the Program
	 */
	public static void main(String[] args) throws IOException {
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
            ClientGUI CG = new ClientGUI(CMH);
			BasicChatPanel BCP = CG.getBCP();
			CMH.addBasicChatPanel(BCP);
		}
	}

}
