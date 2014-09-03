package bb.chat.network;

import bb.chat.interfaces.IChatActor;
import bb.chat.interfaces.IMessageHandler;
import bb.chat.interfaces.IPacket;
import bb.chat.network.packet.Chatting.ChatPacket;

import java.io.IOException;
import java.net.Socket;

/**
 * @author BB20101997
 */
public class ClientMessageHandler extends BasicMessageHandler implements IMessageHandler {
    private Socket socket;

    private IOHandler IRServer = null;

    /**
     * The Constructor ,it adds the basic Command
     */
    public ClientMessageHandler() {

        side = Side.CLIENT;
        localActor = new IChatActor() {

            private String name = "Client";

            @Override
            public String getActorName() {

                return name;
            }

            @Override
            public void setActorName(String s) {

                name = s;
            }

            @Override
            public void disconnect() {

            }
        };
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
                IRServer.disconnect();
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
    public void receivePackage(IPacket p, IChatActor sender) {
        //TODO
        if(p instanceof ChatPacket){
            println(((ChatPacket) p).Sender + " : "+((ChatPacket) p).message);
        }
    }

    @Override
    public void sendPackage(IPacket p) {
        IRServer.sendPackage(p);
    }

    @Override
    public void disconnect(IChatActor ica) {

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
