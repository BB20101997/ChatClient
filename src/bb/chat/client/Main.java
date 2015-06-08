package bb.chat.client;

import bb.chat.gui.ClientGUI;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author BB20101997
 */
class Main {

	/**
	 * @param args just the usual tArgs to start up the Program
	 */
	public static void main(String[] args){
		String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		@SuppressWarnings("StringConcatenationMissingWhitespace") File file = new File("Log"+File.pathSeparator+"Client"+File.pathSeparator+"log-"+date+".fw").getAbsoluteFile();

		new ClientGUI();
	}

}
