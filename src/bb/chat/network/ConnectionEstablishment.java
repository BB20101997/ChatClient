package bb.chat.network;

import bb.chat.interfaces.IMessageHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.rmi.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;


/**
 * @author BB20101997
 */
class ConnectionEstablishment {

	private SSLSocket sock = null;

	/**
	 * @param host the host to Connect to
	 * @param port the port the host is listening to
	 * @param imh  just needed to print out errors to the user may be removed later
	 */
	public ConnectionEstablishment(String host, int port, IMessageHandler imh) {

		try {

			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, null, null);

			SSLSocketFactory ssf = sc.getSocketFactory();
			SSLSocket s = (SSLSocket) ssf.createSocket(host, port);
			bb.chat.util.Socket.enableAnonConnection(s);
			s.startHandshake();
			sock = s;
		} catch(UnknownHostException e) {
			System.err.println("Don't know about host " + host);
			imh.println("Could not connect to Server! \n Please check if host was spelled right!");
		} catch(IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + host);
			imh.println("Could not connect to Server!");
			sock = null;
		} catch(NoSuchAlgorithmException e) {
			System.err.println("Client missing TLS algorithm");
			e.printStackTrace();
		} catch(KeyManagementException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return returns the Socket that has benn created
	 */
	public SSLSocket getSocket() {

		return sock;
	}

}
