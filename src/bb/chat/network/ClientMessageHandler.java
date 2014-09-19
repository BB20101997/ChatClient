package bb.chat.network;

import bb.chat.enums.Side;
import bb.chat.interfaces.IIOHandler;
import bb.chat.interfaces.IPacket;
import bb.chat.interfaces.IUserPermission;
import bb.chat.network.handler.BasicMessageHandler;
import bb.chat.network.handler.DefaultPacketHandler;
import bb.chat.network.handler.IOHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * @author BB20101997
 */
public class ClientMessageHandler extends BasicMessageHandler{
    private Socket socket;

    private IOHandler IRServer = null;

    /**
     * The Constructor ,it adds the basic Command
     */
	@SuppressWarnings("unchecked")
    public ClientMessageHandler() {

        side = Side.CLIENT;
        localActor = new IIOHandler(){

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
			public void setActorName(String name) {
				this.name = name;
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
			public IUserPermission getUserPermission() {
				return null;
			}

			@Override
			public void run() {
			}
		};

		PD.registerPacketHandler(new DefaultPacketHandler(this));

        addCommand(bb.chat.command.Connect.class);
        addCommand(bb.chat.command.Whisper.class);
        addCommand(bb.chat.command.Rename.class);
        addCommand(bb.chat.command.Disconnect.class);
    }

    @Override
    public void connect(String host, int port) {

        if (IRServer != null) {
            disconnect(IRServer);
            try {
                IRServer.stop();
            } catch (Throwable e) {

                e.printStackTrace();
            }
            IRServer = null;
        }

        socket = new ConnectionEstablishment(host, port, this).getSocket();

        if (socket != null) {
            try {
                IRServer = new IOHandler(socket.getInputStream(), socket.getOutputStream(), this);
                IRServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	@Override
	public void sendPackage(IPacket p) {
		IRServer.sendPacket(p);
	}

}
