package bb.chat.network;

import bb.chat.command.*;
import bb.chat.enums.NetworkState;
import bb.chat.enums.Side;
import bb.chat.interfaces.APacket;
import bb.chat.interfaces.IIOHandler;
import bb.chat.network.handler.BasicConnectionHandler;
import bb.chat.network.handler.BasicIOHandler;
import bb.chat.network.handler.DefaultPacketHandler;
import bb.chat.security.BasicUser;

import java.io.IOException;

/**
 * @author BB20101997
 */
public class ClientConnectionHandler extends BasicConnectionHandler {

	protected NetworkState netState = NetworkState.PRE_HANDSHAKE;

	/**
	 * The Constructor ,it adds the basic Command
	 */
	public ClientConnectionHandler() {
		super();

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
			public boolean sendPacket(APacket p) {
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
			public NetworkState getNetworkState() {
				return netState;
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



	}

	@Override
	@SuppressWarnings("unchecked")
	public void initiate() {

		System.out.println("Initiating ClientConnectionHandler:" +this);

		getIChatInstance().getPacketDistributor().registerPacketHandler(new DefaultPacketHandler(this));

		getIChatInstance().getCommandRegestry().addCommand(Whisper.class);
		getIChatInstance().getCommandRegestry().addCommand(Rename.class);
		getIChatInstance().getCommandRegestry().addCommand(Help.class);
		getIChatInstance().getCommandRegestry().addCommand(Save.class);
		getIChatInstance().getCommandRegestry().addCommand(Stop.class);
		getIChatInstance().getCommandRegestry().addCommand(Permission.class);
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
				IRServer = new BasicIOHandler(socket.getInputStream(), socket.getOutputStream(), this, true);
				IRServer.start();
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void sendPackage(APacket p, IIOHandler target) {
		if(IRServer != null) {
			IRServer.sendPacket(p);
		} else {
			System.err.println("Couldn't send Packet no Server!");
		}
	}
}
