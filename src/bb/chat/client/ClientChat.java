package bb.chat.client;

import bb.chat.chat.BasicChat;
import bb.chat.command.*;
import bb.chat.interfaces.IChatActor;
import bb.chat.interfaces.ICommandRegistry;
import bb.chat.network.handler.DefaultPacketHandler;
import bb.chat.security.BasicPermissionRegistrie;
import bb.chat.security.BasicUser;
import bb.chat.security.BasicUserDatabase;
import bb.net.interfaces.IConnectionManager;
import bb.net.interfaces.IIOHandler;

/**
 * Created by BB20101997 on 04.04.2015.
 */
public class ClientChat extends BasicChat {

	public ClientChat(final IConnectionManager imessagehandler, BasicPermissionRegistrie bpr, BasicUserDatabase bud, ICommandRegistry icr) {
		super(imessagehandler, bpr, bud, icr);

		icr.addCommand(Whisper.class);
		icr.addCommand(Rename.class);
		icr.addCommand(Register.class);
		icr.addCommand(List.class);
		icr.addCommand(Help.class);
		icr.addCommand(Save.class);
		icr.addCommand(Stop.class);
		icr.addCommand(Permission.class);

		imessagehandler.getPacketDistributor().registerPacketHandler(new DefaultPacketHandler(this));

		localActor =  new IChatActor(){

			protected String name = "Client";

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
			public boolean setActorName(String name) {
				this.name = name;
				return true;
			}

			@Override
			public boolean isLoggedIn() {
				return false;
			}

			@Override
			public void setUser(BasicUser u) {
			}

			@Override
			public BasicUser getUser() {
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
						localActor.setActorName(name);
					}

					@Override
					public boolean setPassword(String s, BasicUser user) {
						return true;
					}
				};
			}
		};

	}
}
