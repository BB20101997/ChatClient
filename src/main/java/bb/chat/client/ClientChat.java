package bb.chat.client;

import bb.chat.chat.BasicChat;
import bb.chat.command.*;
import bb.chat.interfaces.IChatActor;
import bb.chat.interfaces.ICommandRegistry;
import bb.chat.network.handler.DefaultPacketHandler;
import bb.chat.security.BasicPermissionRegistrie;
import bb.chat.security.BasicUser;
import bb.chat.security.BasicUserDatabase;
import bb.net.handler.BasicConnectionManager;
import bb.net.interfaces.IConnectionManager;
import bb.net.interfaces.IIOHandler;
import bb.util.file.log.BBLogHandler;
import bb.util.file.log.Constants;

import java.util.logging.Logger;

import static bb.chat.client.Constants.LOG_NAME;

/**
 * Created by BB20101997 on 04.04.2015.
 */
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class ClientChat extends BasicChat {

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger log;

	static {
		log = Logger.getLogger(ClientChat.class.getName());
		//noinspection DuplicateStringLiteralInspection
		log.addHandler(new BBLogHandler(Constants.getLogFile(LOG_NAME)));
	}

	public ClientChat(){
		this(new BasicConnectionManager(), new BasicPermissionRegistrie(), new BasicUserDatabase(), new BasicCommandRegistry());
	}

	//simply initialising the super and adding the stuff client specific
	public ClientChat(final IConnectionManager imessagehandler, BasicPermissionRegistrie bpr, BasicUserDatabase bud, ICommandRegistry icr) {
		super(imessagehandler, bpr, bud, icr);
		log.entering("ClientChat","Constructor");
		addDefaultCommandsClient();

		imessagehandler.getPacketDistributor().registerPacketHandler(new DefaultPacketHandler(this));

		//noinspection PublicMethodWithoutLogging
		LOCAL =  new IChatActor(){

			protected volatile String name = "Client";

			@Override
			public IIOHandler getIIOHandler() {
				return imessagehandler.LOCAL();
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
			public boolean setActorName(String newName, boolean not) {
				log.finer("Renaming Clients Local Actor to "+newName);
				name = newName;
				return true;
			}

			@Override
			public boolean isLoggedIn() {
				return false;
			}

			@Override
			public void setUser(BasicUser basicUser) {
			}

			@Override
			public BasicUser getUser() {
				//noinspection PublicMethodWithoutLogging
				return new BasicUser(){
					@Override
					public int getUserID() {
						return -1;
					}

					@Override
					public String getUserName() {
						return name;
					}

					@Override
					public void setUserID(int i) {
					}

					@Override
					public void setUserName(String name) {
						LOCAL.setActorName(name,false);
					}

					@Override
					public void setPassword(String s) {
					}
				};
			}
		};
		log.exiting("ClientChat","Constructor");
	}
}
