package bb.chat.client;

import bb.chat.basis.BasisConstants;
import bb.chat.chat.BasicChat;
import bb.chat.command.BasicCommandRegistry;
import bb.chat.enums.Bundles;
import bb.chat.interfaces.IChatActor;
import bb.chat.interfaces.ICommandRegistry;
import bb.chat.network.handler.DefaultPacketHandler;
import bb.chat.security.BasicPermissionRegistrie;
import bb.chat.security.BasicUser;
import bb.chat.security.BasicUserDatabase;
import bb.net.handler.BasicConnectionManager;
import bb.net.interfaces.IConnectionManager;
import bb.net.interfaces.IIOHandler;

import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Created by BB20101997 on 04.04.2015.
 */
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class ClientChat extends BasicChat {

	@SuppressWarnings("ConstantNamingConvention")
	private static final Logger logger = ClientConstants.getLogger(ClientChat.class);

	public ClientChat(){
		this(new BasicConnectionManager(), new BasicPermissionRegistrie(), new BasicUserDatabase(), new BasicCommandRegistry());
	}

	//simply initialising the super and adding the stuff client specific
	public ClientChat(final IConnectionManager imessagehandler, BasicPermissionRegistrie bpr, BasicUserDatabase bud, ICommandRegistry icr) {
		super(imessagehandler, bpr, bud, icr);
		logger.entering("ClientChat","Constructor");
		addDefaultCommandsClient();

		imessagehandler.getPacketDistributor().registerPacketHandler(new DefaultPacketHandler(this));

		//noinspection PublicMethodWithoutLogging
		LOCAL =  new IChatActor(){

			protected volatile String name = BasisConstants.CLIENT;

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
				logger.finer(MessageFormat.format(Bundles.LOG_TEXT.getString("log.rename.local"), newName));
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
		logger.exiting("ClientChat","Constructor");
	}
}
