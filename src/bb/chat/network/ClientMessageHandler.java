package bb.chat.network;

import bb.chat.interfaces.IIOHandler;
import bb.chat.interfaces.IPacket;
import bb.chat.network.handler.BasicMessageHandler;
import bb.chat.network.handler.DefaultPacketHandler;
import bb.chat.network.handler.IOHandler;
import bb.chat.network.packet.DataOut;

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
    public ClientMessageHandler() {

        side = Side.CLIENT;
        localActor = new IIOHandler(){

			private String name = "Client";

			@Override
			public void start() {
			}

			@Override
			public void stop() {
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
    public void receivePackage(IPacket p, IIOHandler sender) {
		DataOut dataOut = DataOut.newInstance();
		try {
			p.writeToData(dataOut);
		} catch(IOException e) {
			e.printStackTrace();
		}
		PD.distributePacket(PR.getID(p.getClass()),dataOut.getBytes(),sender);
    }

    @Override
    public void sendPackage(IPacket p) {
        IRServer.sendPacket(p);
    }

    @Override
    public void disconnect(IIOHandler ica) {

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
