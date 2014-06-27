package bb.chat.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import bb.chat.gui.BasicChatPanel;
import bb.chat.interfaces.IChatActor;
import bb.chat.interfaces.ICommand;
import bb.chat.interfaces.IMessageHandler;

/**
 * @author BB20101997
 */
public class ClientMessageHandler implements IMessageHandler
{

	List<String>			ComNames	= new ArrayList<String>();

	List<ICommand>			Commands	= new ArrayList<ICommand>();
	List<BasicChatPanel>	BCPList		= new ArrayList<BasicChatPanel>();
	Socket					socket;

	IOHandler				IRServer	= null;
	Thread					t;

	IChatActor				ICActor		= new IChatActor(){

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
										};

	/**
	 * The Constructor ,it adds the basic Commands
	 */
	@SuppressWarnings("deprecation")
	public ClientMessageHandler()
	{

		addCommand(bb.chat.command.Connect.class);
		addCommand(bb.chat.command.Logout.class);
		addCommand(bb.chat.command.Whisper.class);
		addCommand(bb.chat.command.Rename.class);
		addCommand(bb.chat.command.Disconnect.class);
	}

	@Override
	public void addBasicChatPanel(BasicChatPanel BCP)
	{

		BCPList.add(BCP);
	}

	@Override
	public void addCommand(Class<? extends ICommand> c)
	{

		try
		{
			ICommand IC = c.newInstance();
			if(!IC.ServerOnly)
			{
				String s = IC.getName();
				ComNames.add(s);
				Commands.add(IC);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void connect(String host, int port)
	{

		socket = new ConnectionEstablisher(host, port, this).getSocket();

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
		IRServer.end();
		IRServer.removeMessageHandler(this);

	}

	@Override
	public ICommand getCommand(String text)
	{

		String[] name = text.split(" ", 2);
		if(ComNames.contains(name[0].replace("/", ""))) { return Commands.get(ComNames.indexOf(name[0].replace("/", "")));

		}
		return null;
	}

	@Override
	public IChatActor getActor()
	{

		return ICActor;
	}

	@Override
	public void help(ICommand ic, IChatActor sender)
	{

		help(ic.helpCommand(), sender);
	}

	@Override
	public void help(String s, IChatActor sender)
	{

		help(Commands.get(ComNames.indexOf(s)).helpCommand(), sender);
	}

	@Override
	public void help(String[] s, IChatActor sender)
	{

		for(String str : s)
		{
			println(str);
		}
	}

	@Override
	public void helpAll(IChatActor sender)
	{

		for(ICommand c : Commands)
		{
			help(c.getName(), sender);
		}
	}

	@Override
	public void Message(String s, IChatActor sender)
	{

		if(s.startsWith("/"))
		{
			ICommand ic = getCommand(s);
			if(ic != null)
			{
				ic.runCommandClient(s, this);
			}
		}
		else
		{
			sendMessageAll(s, sender);
			println(sender.getActorName() + " : " + s);
		}
		System.out.println(s);

	}

	@Override
	public void print(String s)
	{

		System.out.println(s);
		for(BasicChatPanel bcp : BCPList)
		{
			bcp.print(s);
		}
	}

	@Override
	public void println(String s)
	{

		System.out.println(s);
		for(BasicChatPanel bcp : BCPList)
		{
			bcp.println(s);
		}
	}

	@Override
	public void recieveMessage(String s, IChatActor sender)
	{

		if(s.equals("")) { return; }
		if(s.startsWith("/"))
		{
			ICommand ic = getCommand(s);
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
	public void sendMessage(String text, String Empf, IChatActor Send)
	{

		IRServer.getOut().println("/whisper " + Empf + " " + text);
		IRServer.getOut().flush();
	}

	@Override
	public void sendMessageAll(String text, IChatActor Send)
	{

		IRServer.getOut().println(text);
		IRServer.getOut().flush();
	}
}
