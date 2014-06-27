package bb.chat.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import bb.chat.interfaces.IMessageHandler;

/**
 * @author BB20101997
 */
public class ConnectionEstablisher
{

	private Socket	sock	= null;

	/**
	 * @param host
	 *            the host to Connect to
	 * @param port
	 *            the port the host is listening to
	 * @param imh
	 *            just needed to print out errors to the user may be removed
	 *            later
	 */
	public ConnectionEstablisher(String host, int port, IMessageHandler imh)
	{

		try
		{
			sock = new Socket(host, port);
		}
		catch(UnknownHostException e)
		{
			System.err.println("Don't know about host " + host);
			imh.println("Could not connect to Server! \n Please check if host was spelled right!");
		}
		catch(IOException e)
		{
			System.err.println("Couldn't get I/O for the connection to " + host);
			imh.println("Could not connect to Server!");
		}

	}

	/**
	 * @return returns the Socket that has benn created
	 */
	public Socket getSocket()
	{

		return sock;
	}

}
