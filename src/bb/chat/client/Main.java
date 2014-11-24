package bb.chat.client;

import bb.chat.gui.ClientGUI;

import java.io.IOException;

/**
 * @author BB20101997
 */
class Main {

	/**
	 * @param args just the usual tArgs to start up the Program
	 */
	public static void main(String[] args) throws IOException {
		boolean gui = true;

		for(String s : args) {
			if(s.equals("nogui")) {
				gui = false;
			}
		}


		//noinspection StatementWithEmptyBody
		if(gui) {
			new ClientGUI();
		} else {
			//TODO: DO something else
		}
	}

}
