package bb.chat.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import bb.chat.interfaces.IChatActor;
import bb.chat.interfaces.ICommand;
import bb.chat.interfaces.IMessageHandler;

/**
 * @author BB20101997
 */
public class ClientMessageHandler extends BasicMessageHandler implements IMessageHandler
{

	List<String>	ComNames	= new ArrayList<String>();

	List<ICommand>	Commands	= new ArrayList<ICommand>();
	Socket			socket;

	IOHandler		IRServer	= null;
	Thread			t;

	IChatActor		ICActor		= new IChatActor(){

									private String	name	= "Client";

									@Override
									public String getActorName()
									{

										return name;
									}

									@Override
									public void setActorName(String s)
									{

										name = s;
									}

									@Override
									public void disconnect()
									{

									}
								};

	/**
	 * The Constructor ,it adds the basic Commands
	 */
	public ClientMessageHandler()
	{

		side = Side.CLIENT;
		localActor = ICActor;
		addCommand(bb.chat.command.Connect.class);
		addCommand(bb.chat.command.Whisper.class);
		addCommand(bb.chat.command.Rename.class);
		addCommand(bb.chat.command.Disconnect.class);
	}

	@Override
	public void connect(String host, int port)
	{

		if(IRServer != null)
		{
			disconnect(IRServer);
			try
			{
				IRServer.finalize();
			}
			catch(Throwable e)
			{

				e.printStackTrace();
			}
			IRServer = null;
		}

		socket = new ConnectionEstablisher(host, port, this).getSocket();

		try
		{
			IRServer = new IOHandler(socket.getInputStream(), socket.getOutputStream(), this);
			t = new Thread(IRServer);
			t.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect(IChatActor ica)
	{

		try
		{
			socket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		IRServer.removeMessageHandler(this);

	}

	@Override
	public void recieveMessage(String s, IChatActor sender)
	{

		System.out.println("Recieved : " + s);

		if(s.equals("")) { return; }
		if(s.startsWith("/"))
		{
			String[] strA = s.split(" ");
			ICommand ic = getCommand(strA[0].replace("/", ""));
			if(ic != null)
			{
				ic.runCommandRecievedFromServer(s, this);
			}
		}
		else
		{
			println(s);
		}
	}

	@Override
	public void sendMessage(String text, IChatActor Send)
	{

		if(IRServer != null)
		{
			if(Target != ALL)
			{
				IRServer.getOut().println("/whisper " + Target.getActorName() + " " + text);
			}
			else
			{
				IRServer.getOut().println(text);
			}
			IRServer.getOut().flush();
		}
		else
		{
			println("You are not connected to a Server!\nPlease connect first!");
		}
	}

}
