package bb.chat.network;

import bb.chat.command.*;
import bb.chat.enums.Side;
import bb.chat.interfaces.IIOHandler;
import bb.chat.interfaces.IPacket;
import bb.chat.network.handler.BasicIOHandler;
import bb.chat.network.handler.BasicMessageHandler;
import bb.chat.network.handler.DefaultPacketHandler;
import bb.chat.security.BasicUser;

import java.io.IOException;

/**
 * @author BB20101997
 */
public class ClientMessageHandler extends BasicMessageHandler {

	/**
	 * The Constructor ,it adds the basic Command
	 */
	@SuppressWarnings("unchecked")
	public ClientMessageHandler() {

		side = Side.CLIENT;
		localActor = new IIOHandler() {

			private String name = "Client";

			@Override
			public void start() {
				//TODO:implement
			}

			@Override
			public void stop() {
				//TODO:implement
			}

			@Override
			public boolean isDummy() {
				return true;
			}

			@Override
			public String getActorName() {
				return name;
			}

			@Override
			public boolean setActorName(String name) {
				this.name = name;
				return true;
			}

			@Override
			public boolean sendPacket(IPacket p) {
				return false;
			}

			@Override
			public boolean isAlive() {
				return true;
			}

			@Override
			public void receivedHandshake() {
			}



			@Override
			public boolean isLoggedIn() {
				return false;
			}

			@Override
			public BasicUser getUser() {
				return null;
			}

			@Override
			public void setUser(BasicUser u) {

			}

			@Override
			public void run() {
			}
		};

		PD.registerPacketHandler(new DefaultPacketHandler(this));

		addCommand(Connect.class);
		addCommand(Whisper.class);
		addCommand(Rename.class);
		addCommand(Disconnect.class);
		addCommand(Help.class);
		addCommand(Login.class);
		addCommand(Register.class);
		addCommand(Save.class);
		addCommand(Stop.class);
		addCommand(Permission.class);
	}

	@Override
	public boolean connect(String host, int port) {

		if(IRServer != null) {
			disconnect(IRServer);
			try {
				IRServer.stop();
			} catch(Throwable e) {

				e.printStackTrace();
			}
			IRServer = null;
		}

		socket = new ConnectionEstablishment(host, port, this).getSocket();

		if(socket != null) {
			try {
				IRServer = new BasicIOHandler(socket.getInputStream(), socket.getOutputStream(), this);
				IRServer.start();
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		else{
			return false;
		}
		return true;
	}

	@Override
	public void sendPackage(IPacket p) {
		IRServer.sendPacket(p);
	}

}
