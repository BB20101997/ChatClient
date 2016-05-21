package bb.chat.client;

import bb.chat.gui.ClientGUI;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author BB20101997
 */
@SuppressWarnings({"ClassNamingConvention", "ClassWithoutLogger"})
class Main {

	/**
	 * @param args just the usual tArgs to start up the Program
	 */
	public static void main(String[] args){
		Logger.getLogger("").setLevel(Level.ALL);
		new ClientGUI();
	}

}
